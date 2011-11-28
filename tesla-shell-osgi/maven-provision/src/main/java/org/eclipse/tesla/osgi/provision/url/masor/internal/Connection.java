/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.masor.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.tesla.osgi.provision.url.masor.MavenArtifactSetObrRepository;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class Connection
    extends URLConnection
{

    private MavenArtifactSetObrRepository mavenArtifactSetObrRepository;

    public Connection( final MavenArtifactSetObrRepository mavenArtifactSetObrRepository,
                       final URL url )
    {
        super( url );
        this.mavenArtifactSetObrRepository = mavenArtifactSetObrRepository;
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
        return mavenArtifactSetObrRepository.openStream( url.getPath() );
    }

}
