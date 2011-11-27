package org.eclipse.tesla.osgi.provision.internal;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * TODO
 *
 * @since 1.0
 */
public class URLAwareInputStream
    extends FilterInputStream
{

    private final URL url;

    public URLAwareInputStream( final URL url, final InputStream inputStream )
    {
        super( inputStream );
        this.url = url;
    }

    public URL getUrl()
    {
        return url;
    }

}
