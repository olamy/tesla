package org.eclipse.tesla.shell.provision.url.mab;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.eclipse.tesla.shell.provision.Storage;
import org.eclipse.tesla.shell.provision.internal.MavenLikePathResolver;
import org.eclipse.tesla.shell.provision.internal.TempDirStorage;
import org.eclipse.tesla.shell.provision.url.mab.internal.Connection;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
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

    private Storage storage;

    private MavenLikePathResolver pathResolver;

    private MavenModelResolver modelResolver;

    private MavenArtifactResolver artifactResolver;

    public Handler()
    {
        storage = new TempDirStorage( new TempDirStorage.TempDir() );
        final ServiceLocator serviceLocator = new DefaultServiceLocator()
        {
            {
                setService( RepositoryConnectorFactory.class, AsyncRepositoryConnectorFactory.class );
            }
        };
        pathResolver = new MavenLikePathResolver( serviceLocator );
        final DefaultMavenSettingsFactory settingsFactory = new DefaultMavenSettingsFactory( serviceLocator );
        final Provider<RepositorySystemSession> sessionProvider = new Provider<RepositorySystemSession>()
        {
            @Override
            public RepositorySystemSession get()
            {
                return new MavenBridgeRepositorySystemSession( serviceLocator );
            }
        };
        final RemoteMavenArtifactResolverUsingSettings remoteArtifactResolver =
            new RemoteMavenArtifactResolverUsingSettings( serviceLocator, settingsFactory, sessionProvider );
        artifactResolver = remoteArtifactResolver;
        modelResolver = new RemoteMavenModelResolverUsingSettings(
            remoteArtifactResolver,
            sessionProvider
        );
    }

    @Inject
    Handler( final Storage storage,
             final MavenModelResolver modelResolver,
             final MavenArtifactResolver artifactResolver )
    {
        this.storage = storage;
        this.modelResolver = modelResolver;
        this.artifactResolver = artifactResolver;
    }

    @Override
    protected URLConnection openConnection( final URL url )
        throws IOException
    {
        return new Connection( storage, pathResolver, modelResolver, artifactResolver, url );
    }

}
