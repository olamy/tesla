package org.eclipse.tesla.shell.provision.url.masor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.eclipse.tesla.shell.provision.internal.TempDirStorage;
import org.eclipse.tesla.shell.provision.url.masor.internal.Connection;
import org.eclipse.tesla.shell.provision.url.masor.internal.DefaultMavenArtifactSetObrRepository;
import org.eclipse.tesla.shell.provision.url.masor.internal.Sha1Digester;

/**
 * TODO
 *
 * @since 1.0
 */
public class Handler
    extends URLStreamHandler
{

    private MavenArtifactSetObrRepository mavenArtifactSetObrRepository;

    public Handler()
    {
        mavenArtifactSetObrRepository = new DefaultMavenArtifactSetObrRepository(
            new TempDirStorage( new TempDirStorage.TempDir() ),
            new Sha1Digester()
        );
    }

    @Override
    protected URLConnection openConnection( final URL url )
        throws IOException
    {
        return new Connection( mavenArtifactSetObrRepository, url );
    }

}
