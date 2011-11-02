package org.eclipse.tesla.shell.provision.url.maor;

import java.io.InputStream;

/**
 * TODO
 *
 * @since 1.0
 */
public interface MavenArtifactObrRepository
{

    String create( String coordinates );

    InputStream openStream( String path );

}
