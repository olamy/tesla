package org.apache.maven.repository.internal;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.eventspy.internal.EventSpyDispatcher;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.aether.ConfigurationProperties;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.graph.manager.ClassicDependencyManager;
import org.sonatype.aether.util.graph.selector.AndDependencySelector;
import org.sonatype.aether.util.graph.selector.ExclusionDependencySelector;
import org.sonatype.aether.util.graph.selector.OptionalDependencySelector;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;
import org.sonatype.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.sonatype.aether.util.graph.transformer.ConflictMarker;
import org.sonatype.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.sonatype.aether.util.graph.transformer.JavaEffectiveScopeCalculator;
import org.sonatype.aether.util.graph.transformer.NearestVersionConflictResolver;
import org.sonatype.aether.util.graph.traverser.FatArtifactTraverser;
import org.sonatype.aether.util.repository.DefaultAuthenticationSelector;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 */
@Component( role = RepositorySystemSessionFactory.class )
public class DefaultRepositorySystemSessionFactory
    implements RepositorySystemSessionFactory
{

    @Requirement
    private Logger logger;

    @Requirement
    private PlexusContainer container;

    @Requirement
    private SettingsDecrypter settingsDecrypter;

    @Requirement( optional = true, hint = "ide" )
    private WorkspaceReader workspaceRepository;

    @Requirement
    private RepositorySystem repoSystem;

    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Requirement
    private EventSpyDispatcher eventSpyDispatcher;

    public RepositorySystemSession newRepositorySession( MavenExecutionRequest request )
    {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        session.setCache( request.getRepositoryCache() );

        session.setIgnoreInvalidArtifactDescriptor( true ).setIgnoreMissingArtifactDescriptor( true );

        Map<Object, Object> configProps = new LinkedHashMap<Object, Object>();
        configProps.put( ConfigurationProperties.USER_AGENT, getUserAgent() );
        configProps.put( ConfigurationProperties.INTERACTIVE, Boolean.valueOf( request.isInteractiveMode() ) );
        configProps.putAll( request.getSystemProperties() );
        configProps.putAll( request.getUserProperties() );

        session.setOffline( request.isOffline() );
        session.setChecksumPolicy( request.getGlobalChecksumPolicy() );
        session.setUpdatePolicy( request.isUpdateSnapshots() ? RepositoryPolicy.UPDATE_POLICY_ALWAYS : null );

        session.setNotFoundCachingEnabled( request.isCacheNotFound() );
        session.setTransferErrorCachingEnabled( request.isCacheTransferError() );

        session.setArtifactTypeRegistry( RepositoryUtils.newArtifactTypeRegistry( artifactHandlerManager ) );

        LocalRepository localRepo = new LocalRepository( request.getLocalRepository().getBasedir() );
        session.setLocalRepositoryManager( repoSystem.newLocalRepositoryManager( localRepo ) );

        if ( request.getWorkspaceReader() != null )
        {
            session.setWorkspaceReader( request.getWorkspaceReader() );
        }
        else
        {
            session.setWorkspaceReader( workspaceRepository );
        }

        DefaultSettingsDecryptionRequest decrypt = new DefaultSettingsDecryptionRequest();
        decrypt.setProxies( request.getProxies() );
        decrypt.setServers( request.getServers() );
        SettingsDecryptionResult decrypted = settingsDecrypter.decrypt( decrypt );

        if ( logger.isDebugEnabled() )
        {
            for ( SettingsProblem problem : decrypted.getProblems() )
            {
                logger.debug( problem.getMessage(), problem.getException() );
            }
        }

        DefaultMirrorSelector mirrorSelector = new DefaultMirrorSelector();
        for ( Mirror mirror : request.getMirrors() )
        {
            mirrorSelector.add( mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(),
                                mirror.getMirrorOfLayouts() );
        }
        session.setMirrorSelector( mirrorSelector );

        DefaultProxySelector proxySelector = new DefaultProxySelector();
        for ( Proxy proxy : decrypted.getProxies() )
        {
            Authentication proxyAuth = new Authentication( proxy.getUsername(), proxy.getPassword() );
            proxySelector.add( new org.sonatype.aether.repository.Proxy( proxy.getProtocol(), proxy.getHost(),
                                                                         proxy.getPort(), proxyAuth ),
                               proxy.getNonProxyHosts() );
        }
        session.setProxySelector( proxySelector );

        DefaultAuthenticationSelector authSelector = new DefaultAuthenticationSelector();
        for ( Server server : decrypted.getServers() )
        {
            Authentication auth =
                new Authentication( server.getUsername(), server.getPassword(), server.getPrivateKey(),
                                    server.getPassphrase() );
            authSelector.add( server.getId(), auth );

            if ( server.getConfiguration() != null )
            {
                Xpp3Dom dom = (Xpp3Dom) server.getConfiguration();
                for ( int i = dom.getChildCount() - 1; i >= 0; i-- )
                {
                    Xpp3Dom child = dom.getChild( i );
                    if ( "wagonProvider".equals( child.getName() ) )
                    {
                        dom.removeChild( i );
                    }
                }

                XmlPlexusConfiguration config = new XmlPlexusConfiguration( dom );
                configProps.put( "aether.connector.wagon.config." + server.getId(), config );
            }

            configProps.put( "aether.connector.perms.fileMode." + server.getId(), server.getFilePermissions() );
            configProps.put( "aether.connector.perms.dirMode." + server.getId(), server.getDirectoryPermissions() );
        }
        session.setAuthenticationSelector( authSelector );

        DependencyTraverser depTraverser = new FatArtifactTraverser();
        session.setDependencyTraverser( depTraverser );

        DependencyManager depManager = new ClassicDependencyManager();
        session.setDependencyManager( depManager );

        DependencySelector depFilter =
            new AndDependencySelector( new ScopeDependencySelector( "test", "provided" ),
                                       new OptionalDependencySelector(), new ExclusionDependencySelector() );
        session.setDependencySelector( depFilter );

        DependencyGraphTransformer transformer =
            new ChainedDependencyGraphTransformer( new ConflictMarker(), new JavaEffectiveScopeCalculator(),
                                                   new NearestVersionConflictResolver(),
                                                   new JavaDependencyContextRefiner() );
        session.setDependencyGraphTransformer( transformer );

        session.setTransferListener( request.getTransferListener() );

        session.setRepositoryListener( eventSpyDispatcher.chainListener( new LoggingRepositoryListener( logger ) ) );

        session.setUserProps( request.getUserProperties() );
        session.setSystemProps( request.getSystemProperties() );
        session.setConfigProps( configProps );

        for ( RepositorySystemSessionFactoryDelegate delegate : getDelegates() )
        {
            delegate.initRepositorySystemSession( session, request );
        }

        return session;
    }

    private String getUserAgent()
    {
        StringBuilder buffer = new StringBuilder( 128 );

        buffer.append( "Apache-Maven/" ).append( getMavenVersion() );
        buffer.append( " (" );
        buffer.append( "Java " ).append( System.getProperty( "java.version" ) );
        buffer.append( "; " );
        buffer.append( System.getProperty( "os.name" ) ).append( " " ).append( System.getProperty( "os.version" ) );
        buffer.append( ")" );

        return buffer.toString();
    }

    private String getMavenVersion()
    {
        Properties props = new Properties();

        InputStream is = getClass().getResourceAsStream( "/META-INF/maven/org.apache.maven/maven-core/pom.properties" );
        if ( is != null )
        {
            try
            {
                props.load( is );
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to read Maven version", e );
            }
            IOUtil.close( is );
        }

        return props.getProperty( "version", "unknown-version" );
    }

    private List<RepositorySystemSessionFactoryDelegate> getDelegates()
    {
        try
        {
            return container.lookupList( RepositorySystemSessionFactoryDelegate.class );
        }
        catch ( ComponentLookupException e )
        {
            logger.error( "Failed to lookup repository system session delegates: " + e.getMessage(), e );

            return Collections.emptyList();
        }
    }

}
