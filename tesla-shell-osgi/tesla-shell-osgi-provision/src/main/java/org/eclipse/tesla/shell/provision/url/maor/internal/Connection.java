package org.eclipse.tesla.shell.provision.url.maor.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.tesla.shell.provision.url.maor.MavenArtifactObrRepository;
import com.ning.http.client.ProxyServer;

/**
 * TODO
 *
 * @since 1.0
 */
public class Connection
    extends URLConnection
{

    public static final String PROTOCOL = "maor";

    private MavenArtifactObrRepository mavenArtifactObrRepository;

    public Connection( final MavenArtifactObrRepository mavenArtifactObrRepository,
                       final URL url )
    {
        super( url );
        this.mavenArtifactObrRepository = mavenArtifactObrRepository;
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
        mavenArtifactObrRepository.create( url.getPath() );
        return mavenArtifactObrRepository.openStream( url.getPath() );
    }

}
