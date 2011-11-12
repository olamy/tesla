package org.eclipse.tesla.osgi.provision.url.masor;

import java.io.InputStream;

/**
 * TODO
 *
 * @since 1.0
 */
public interface MavenArtifactSetObrRepository
{

    String create( String... coordinates );

    InputStream openStream( String path );

}
