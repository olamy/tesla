package org.eclipse.tesla.shell.provision.url.masor;

import java.io.InputStream;

/**
 * TODO
 *
 * @since 1.0
 */
public interface MavenArtifactSetObrRepository
{

    static final String MASOR_PROTOCOL = "masor";

    String create( String... coordinates );

    InputStream openStream( String path );

}
