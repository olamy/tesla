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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.InvalidRepositoryException;
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
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsSource;
import org.apache.maven.settings.io.SettingsWriter;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import org.sonatype.guice.plexus.config.PlexusBeanConverter;
import org.sonatype.guice.plexus.converters.PlexusXmlBeanConverter;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

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

    @Requirement( role = PlexusContainer.class )
    private DefaultPlexusContainer plexus;

    @Requirement
    private SessionRealmCache sessionRealmCache;

    @Requirement
    private SessionExtensionRealmCache sessionExtensionRealmCache;

    @Requirement
    private SettingsBuilder settingsBuilder;

    @Requirement
    private SettingsWriter settingsWriter;

    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private RuntimeInformation runtimeInformation;

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

                    if ( extension.getPrerequisites() != null && extension.getPrerequisites().getMaven() != null )
                    {
                        if ( !runtimeInformation.isMavenVersion( extension.getPrerequisites().getMaven() ) )
                        {
                            log.debug( "Ignoring incompatible extension defined in " + extensionFile.getAbsolutePath() );
                            continue;
                        }
                    }

                    extension.setId( extensionFile.getName().substring( 0, extensionFile.getName().length() - 4 ) );
                    extension.setLocation( extensionFile.getAbsolutePath() );

                    ClassRealm extensionRealm = createExtensionRealm( request, repoSession, extension );

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

    private Properties getMergedProperties( MavenExecutionRequest request, Settings settings )
    {
        Properties merged = new Properties();

        merged.putAll( request.getSystemProperties() );
        merged.putAll( request.getUserProperties() );

        // TODO does this guarantee proper precedence of properties defined in extension.xml file?
        for ( String profileId : settings.getActiveProfiles() )
        {
            Profile profile = (Profile) settings.getProfilesAsMap().get( profileId );
            merged.putAll( profile.getProperties() );
        }

        return merged;
    }

    private List<RemoteRepository> getMergedRepositores( MavenExecutionRequest request, Settings settings,
                                                         CoreExtension extension )
        throws InvalidRepositoryException
    {
        List<ArtifactRepository> repositories = new ArrayList<ArtifactRepository>();

        for ( org.apache.maven.extension.Repository repository : extension.getPluginRepositories() )
        {
            repositories.add( toArtifactRepository( repository ) );
        }

        repositorySystem.injectMirror( repositories, request.getMirrors() );
        repositorySystem.injectProxy( repositories, request.getProxies() );
        repositorySystem.injectAuthentication( repositories, settings.getServers() );

        Map<String, RemoteRepository> merged = new LinkedHashMap<String, RemoteRepository>();

        toRemoteRepositories( merged, repositories );
        toRemoteRepositories( merged, request.getPluginArtifactRepositories() );

        return new ArrayList<RemoteRepository>( merged.values() );
    }

    public void toRemoteRepositories( Map<String, RemoteRepository> merged,
                                      List<ArtifactRepository> pluginArtifactRepositories )
    {
        for ( ArtifactRepository repository : pluginArtifactRepositories )
        {
            RemoteRepository repo = RepositoryUtils.toRepo( repository );
            if ( !merged.containsKey( repo.getId() ) )
            {
                merged.put( repo.getId(), repo );
            }
        }
    }

    private ClassRealm createExtensionRealm( MavenExecutionRequest request, final RepositorySystemSession repoSession,
                                             final CoreExtension extension )
        throws PluginResolutionException, PluginContainerException
    {
        Plugin plugin = new Plugin();
        plugin.setGroupId( extension.getGroupId() );
        plugin.setArtifactId( extension.getArtifactId() );
        plugin.setVersion( extension.getVersion() );

        final Settings settings;
        try
        {
            settings = getMergedSettings( request, extension );
        }
        catch ( SettingsBuildingException e )
        {
            // this is unlikely to happen because at this point settings should have been processed already
            throw new PluginResolutionException( plugin, e );
        }

        List<RemoteRepository> repositories;
        try
        {
            repositories = getMergedRepositores( request, settings, extension );
        }
        catch ( InvalidRepositoryException e )
        {
            throw new PluginResolutionException( plugin, e );
        }

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

                final Properties properties = getMergedProperties( request, settings );

                final PlexusBeanConverter converter = new PlexusBeanConverter()
                {
                    @Inject
                    private PlexusXmlBeanConverter xmlConverter;

                    @SuppressWarnings( { "unchecked", "rawtypes" } )
                    public Object convert( final TypeLiteral role, final String value )
                    {
                        if ( value.startsWith( "${" ) && value.endsWith( "}" ) )
                        {
                            String key = value.substring( 2, value.length() - 1 );

                            if ( "settings".equals( key ) )
                            {
                                return settings;
                            }
                            else if ( properties.containsKey( key ) )
                            {
                                return xmlConverter.convert( role, properties.getProperty( key ) );
                            }
                        }

                        return xmlConverter.convert( role, value );
                    }

                };

                plexus.discoverComponents( realm, new AbstractModule()
                {
                    @Override
                    protected void configure()
                    {
                        bind( PlexusBeanConverter.class ).toInstance( converter );
                    }
                } );

                sessionExtensionRealmCache.put( cacheKey, realm );

                return realm;
            }
            else
            {
                return cacheRecord.realm;
            }
        }
    }

    private ArtifactRepository toArtifactRepository( Repository repository )
        throws InvalidRepositoryException
    {
        org.apache.maven.model.Repository modelRepository =
            SettingsUtils.convertFromSettingsRepository( toSettingsRepository( repository ) );

        return repositorySystem.buildArtifactRepository( modelRepository );
    }

    private Server toServer( org.apache.maven.extension.Server extServer )
    {
        Server server = new Server();

        server.setId( extServer.getId() );
        server.setUsername( extServer.getUsername() );
        server.setPassword( extServer.getPassword() );
        server.setPrivateKey( extServer.getPrivateKey() );
        server.setPassphrase( extServer.getPassphrase() );

        return server;
    }

    private Settings getMergedSettings( MavenExecutionRequest mavenRequest, CoreExtension extension )
        throws SettingsBuildingException
    {
        SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();

        settingsRequest.setGlobalSettingsFile( mavenRequest.getGlobalSettingsFile() );
        settingsRequest.setUserSettingsFile( mavenRequest.getUserSettingsFile() );
        settingsRequest.setSystemProperties( mavenRequest.getSystemProperties() );
        settingsRequest.setUserProperties( mavenRequest.getUserProperties() );

        settingsRequest.addCustomSettingsSource( toSettingsSource( extension ) );

        // TODO does this consider session activate profiles? should it?

        return settingsBuilder.build( settingsRequest ).getEffectiveSettings();
    }

    private SettingsSource toSettingsSource( CoreExtension extension )
    {
        Settings settings = new Settings();

        for ( org.apache.maven.extension.Server server : extension.getServers() )
        {
            settings.addServer( toServer( server ) );
        }

        Profile profile = new Profile();

        profile.setId( extension.getId() );

        for ( Repository repository : extension.getPluginRepositories() )
        {
            profile.addPluginRepository( toSettingsRepository( repository ) );
        }

        profile.getProperties().putAll( extension.getProperties() );

        settings.addProfile( profile );
        settings.addActiveProfile( profile.getId() );

        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        try
        {
            settingsWriter.write( buf, null, settings );
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( "Failed to serialize settings to memory", e );
        }

        final String location = extension.getLocation();

        final byte[] bytes = buf.toByteArray();

        SettingsSource result = new SettingsSource()
        {
            public String getLocation()
            {
                return location;
            }

            public InputStream getInputStream()
                throws IOException
            {
                return new ByteArrayInputStream( bytes );
            }
        };

        return result;
    }

    private static org.apache.maven.settings.Repository toSettingsRepository( Repository repository )
    {
        org.apache.maven.settings.Repository result = new org.apache.maven.settings.Repository();
        result.setId( repository.getId() );
        result.setLayout( repository.getLayout() );
        result.setUrl( repository.getUrl() );

        result.setSnapshots( toSettingsRepositoryPolicy( repository.getSnapshots() ) );
        result.setReleases( toSettingsRepositoryPolicy( repository.getReleases() ) );

        return result;
    }

    private static org.apache.maven.settings.RepositoryPolicy toSettingsRepositoryPolicy( org.apache.maven.extension.RepositoryPolicy policy )
    {
        if ( policy == null )
        {
            return null;
        }

        org.apache.maven.settings.RepositoryPolicy result = new org.apache.maven.settings.RepositoryPolicy();

        result.setEnabled( policy.isEnabled() );
        result.setChecksumPolicy( policy.getChecksumPolicy() );
        result.setUpdatePolicy( policy.getUpdatePolicy() );

        return result;
    }
}
