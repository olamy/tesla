package org.eclipse.tesla.shell.provision;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.tesla.shell.provision.internal.TempDirStorage;
import com.google.inject.ImplementedBy;

/**
 * TODO
 *
 * @since 1.0
 */
@ImplementedBy( TempDirStorage.class )
public interface Storage
{

    InputStream inputStreamFor( String path );

    OutputStream outputStreamFor( String path );

    boolean exists( String path );

}
