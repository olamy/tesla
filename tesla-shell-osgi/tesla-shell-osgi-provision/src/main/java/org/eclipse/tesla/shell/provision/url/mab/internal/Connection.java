package org.eclipse.tesla.shell.provision.url.mab.internal;

import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;

/**
 * TODO
 *
 * @since 1.0
 */
public class Connection
    extends URLConnection
{

    private MavenModelResolver modelResolver;

    private MavenArtifactResolver artifactResolver;

    public Connection( final MavenModelResolver modelResolver,
                       final MavenArtifactResolver artifactResolver,
                       final URL url )
    {
        super( url );
        this.modelResolver = modelResolver;
        this.artifactResolver = artifactResolver;
    }

    @Override
    public void connect()
        throws IOException
    {
        // ignore
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        try
        {
            final Artifact artifact = artifactResolver.resolveArtifact( request().artifact( url.getPath() ) );
            return new FileInputStream( artifact.getFile() );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new IOException( e );
        }
    }

}
