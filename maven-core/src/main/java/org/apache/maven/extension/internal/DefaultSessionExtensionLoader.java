package org.apache.maven.extension.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.extension.CoreExtension;
import org.apache.maven.extension.Repository;
import org.apache.maven.extension.io.xpp3.CoreExtensionXpp3Reader;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.PluginContainerException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.internal.PluginDependenciesResolver;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

/**
 * @author Benjamin Bentmann
 */
@Component( role = SessionExtensionLoader.class )
public class DefaultSessionExtensionLoader
    implements SessionExtensionLoader
{

    @Requirement
    private Logger log;

    @Requirement
    private PluginDependenciesResolver pluginDependenciesResolver;

    @Requirement
    private ClassRealmManager classRealmManager;

    @Requirement
    private PlexusContainer plexus;

    @Requirement
    private SessionRealmCache sessionRealmCache;

    @Requirement
    private SessionExtensionRealmCache sessionExtensionRealmCache;

    public ClassLoader loadExtensions( MavenExecutionRequest request, RepositorySystemSession repoSession )
        throws PluginResolutionException, PluginContainerException
    {
        ClassRealm sessionRealm = null;

        List<ClassRealm> extensionRealms = new ArrayList<ClassRealm>();

        File extensionDirectory = request.getExtensionDirectory();
        File[] extensionFiles = ( extensionDirectory != null ) ? extensionDirectory.listFiles() : null;
        if ( extensionFiles != null && extensionFiles.length > 0 )
        {
            Arrays.sort( extensionFiles );

            CoreExtensionXpp3Reader parser = new CoreExtensionXpp3Reader();
            for ( File extensionFile : extensionFiles )
            {
                if ( extensionFile.isFile() && extensionFile.getName().endsWith( ".xml" ) )
                {
                    CoreExtension extension;

                    InputStream is = null;
                    try
                    {
                        is = new FileInputStream( extensionFile );
                        extension = parser.read( is );
                    }
                    catch ( IOException e )
                    {
                        log.warn( "Failed to read extension descriptor " + extensionFile + ": " + e.getMessage() );
                        continue;
                    }
                    catch ( XmlPullParserException e )
                    {
                        log.warn( "Failed to parse extension descriptor " + extensionFile + ": " + e.getMessage() );
                        continue;
                    }
                    finally
                    {
                        IOUtil.close( is );
                    }

                    Plugin plugin = new Plugin();
                    plugin.setGroupId( extension.getGroupId() );
                    plugin.setArtifactId( extension.getArtifactId() );
                    plugin.setVersion( extension.getVersion() );

                    List<RemoteRepository> repositories = getMergedRepositores( request, extension );

                    ClassRealm extensionRealm = createExtensionRealm( plugin, repositories, repoSession );

                    extensionRealms.add( extensionRealm );
                }
            }
        }

        if ( !extensionRealms.isEmpty() )
        {
            log.debug( "Extension realms for session: " + extensionRealms );

            synchronized ( sessionRealmCache )
            {
                SessionRealmCache.Key cacheKey = sessionRealmCache.createKey( extensionRealms );
                SessionRealmCache.CacheRecord cacheRecord = sessionRealmCache.get( cacheKey );
                if ( cacheRecord == null )
                {
                    sessionRealm = classRealmManager.createSessionRealm();

                    for ( ClassRealm extensionRealm : extensionRealms )
                    {
                        sessionRealm.importFrom( extensionRealm, extensionRealm.getId() );
                    }

                    sessionRealmCache.put( cacheKey, sessionRealm );
                }
                else
                {
                    sessionRealm = cacheRecord.realm;
                }
            }
        }
        else
        {
            log.debug( "Extension realms for session: (none)" );
        }

        return sessionRealm;
    }

    private List<RemoteRepository> getMergedRepositores( MavenExecutionRequest request, CoreExtension extension )
    {
        Map<String, RemoteRepository> merged = new LinkedHashMap<String, RemoteRepository>();

        for ( org.apache.maven.extension.Repository repository : extension.getPluginRepositories() )
        {
            RemoteRepository repo = toRemoteRepository( repository );
            merged.put( repo.getId(), repo );
        }

        for ( ArtifactRepository repository : request.getPluginArtifactRepositories() )
        {
            RemoteRepository repo = RepositoryUtils.toRepo( repository );
            if ( !merged.containsKey( repo.getId() ) )
            {
                merged.put( repo.getId(), repo );
            }
        }

        return new ArrayList<RemoteRepository>( merged.values() );
    }

    private ClassRealm createExtensionRealm( Plugin plugin, List<RemoteRepository> repositories,
                                             RepositorySystemSession repoSession )
        throws PluginResolutionException, PluginContainerException
    {
        DependencyNode root = pluginDependenciesResolver.resolve( plugin, null, null, repositories, repoSession );
        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        root.accept( nlg );
        List<Artifact> artifacts = nlg.getArtifacts( false );

        synchronized ( sessionExtensionRealmCache )
        {
            SessionExtensionRealmCache.Key cacheKey = sessionExtensionRealmCache.createKey( artifacts );
            SessionExtensionRealmCache.CacheRecord cacheRecord = sessionExtensionRealmCache.get( cacheKey );
            if ( cacheRecord == null )
            {
                ClassRealm realm = classRealmManager.createSessionExtensionRealm( plugin, artifacts );

                try
                {
                    plexus.discoverComponents( realm );
                }
                catch ( PlexusConfigurationException e )
                {
                    throw new PluginContainerException( plugin, realm, "Error in component graph of session extension "
                        + plugin.getId() + ": " + e.getMessage(), e );
                }

                sessionExtensionRealmCache.put( cacheKey, realm );

                return realm;
            }
            else
            {
                return cacheRecord.realm;
            }
        }
    }

    private static RemoteRepository toRemoteRepository( Repository repository )
    {
        RemoteRepository result =
            new RemoteRepository( repository.getId(), repository.getLayout(), repository.getUrl() );
        result.setPolicy( true, toRepositoryPolicy( repository.getSnapshots() ) );
        result.setPolicy( false, toRepositoryPolicy( repository.getReleases() ) );
        return result;
    }

    private static RepositoryPolicy toRepositoryPolicy( org.apache.maven.extension.RepositoryPolicy policy )
    {
        boolean enabled = true;
        String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
        String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

        if ( policy != null )
        {
            enabled = policy.isEnabled();
            if ( policy.getUpdatePolicy() != null )
            {
                updates = policy.getUpdatePolicy();
            }
            if ( policy.getChecksumPolicy() != null )
            {
                checksums = policy.getChecksumPolicy();
            }
        }

        return new RepositoryPolicy( enabled, updates, checksums );
    }

}
