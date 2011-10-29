package org.eclipse.tesla.shell.provision;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Storage
{

    InputStream inputStreamFor( String path );

    OutputStream outputStreamFor( String path );

    boolean exists( String path );

}
