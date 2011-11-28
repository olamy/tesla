/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.maor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import javax.inject.Provider;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.eclipse.tesla.osgi.provision.internal.TempDirStorage;
import org.eclipse.tesla.osgi.provision.url.maor.internal.Connection;
import org.eclipse.tesla.osgi.provision.url.maor.internal.DefaultMavenArtifactObrRepository;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.dependency.RemoteMavenDependencyTreeResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.model.RemoteMavenModelResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.session.MavenBridgeRepositorySystemSession;
import org.sonatype.sisu.maven.bridge.support.settings.DefaultMavenSettingsFactory;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class Handler
    extends URLStreamHandler
{

    private MavenArtifactObrRepository mavenArtifactObrRepository;

    public Handler()
    {
        final ServiceLocator serviceLocator = new DefaultServiceLocator()
        {
            {
                setService( RepositoryConnectorFactory.class, AsyncRepositoryConnectorFactory.class );
            }
        };
        final DefaultMavenSettingsFactory settingsFactory = new DefaultMavenSettingsFactory( serviceLocator );

        this.mavenArtifactObrRepository = new DefaultMavenArtifactObrRepository(
            new TempDirStorage( new TempDirStorage.TempDir() ),
            new RemoteMavenDependencyTreeResolverUsingSettings(
                serviceLocator,
                settingsFactory,
                new RemoteMavenModelResolverUsingSettings(
                    new RemoteMavenArtifactResolverUsingSettings(
                        serviceLocator,
                        settingsFactory
                    )
                ),
                new Provider<RepositorySystemSession>()
                {
                    @Override
                    public RepositorySystemSession get()
                    {
                        return new MavenBridgeRepositorySystemSession( serviceLocator );
                    }
                }
            )
        );
    }

    @Override
    protected URLConnection openConnection( final URL url )
        throws IOException
    {
        return new Connection( mavenArtifactObrRepository, url );
    }

}
