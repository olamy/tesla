/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.masor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.eclipse.tesla.osgi.provision.internal.TempDirStorage;
import org.eclipse.tesla.osgi.provision.url.masor.internal.Connection;
import org.eclipse.tesla.osgi.provision.url.masor.internal.DefaultMavenArtifactSetObrRepository;
import org.eclipse.tesla.osgi.provision.url.masor.internal.Sha1Digester;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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
