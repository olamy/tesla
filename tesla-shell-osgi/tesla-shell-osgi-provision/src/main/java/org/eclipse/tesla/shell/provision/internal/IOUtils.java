package org.eclipse.tesla.shell.provision.internal;

import java.io.Closeable;
import java.io.IOException;

/**
 * TODO
 *
 * @since 1.0
 */
public class IOUtils
{

    public static void close( Closeable closeable )
    {
        if ( closeable != null )
        {
            try
            {
                closeable.close();
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

}
