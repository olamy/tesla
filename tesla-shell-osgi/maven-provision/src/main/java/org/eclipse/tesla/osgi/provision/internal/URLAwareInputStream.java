/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
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
