package org.eclipse.tesla.osgi.provision.url.masor.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.tesla.osgi.provision.url.masor.MavenArtifactSetObrRepository;

/**
 * TODO
 *
 * @since 1.0
 */
public class Connection
    extends URLConnection
{

    private MavenArtifactSetObrRepository mavenArtifactSetObrRepository;

    public Connection( final MavenArtifactSetObrRepository mavenArtifactSetObrRepository,
                       final URL url )
    {
        super( url );
        this.mavenArtifactSetObrRepository = mavenArtifactSetObrRepository;
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
        return mavenArtifactSetObrRepository.openStream( url.getPath() );
    }

}
