package org.eclipse.tesla.shell.provision.url.maor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.eclipse.tesla.shell.provision.internal.TempDirStorage;
import org.eclipse.tesla.shell.provision.url.maor.internal.Connection;
import org.eclipse.tesla.shell.provision.url.maor.internal.DefaultMavenArtifactObrRepository;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.dependency.RemoteMavenDependencyTreeResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.model.RemoteMavenModelResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.session.MavenBridgeRepositorySystemSession;
import org.sonatype.sisu.maven.bridge.support.settings.DefaultMavenSettingsFactory;

/**
 * TODO
 *
 * @since 1.0
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
