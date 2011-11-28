/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.mab.internal;

import static org.eclipse.tesla.osgi.provision.url.mab.Constants.PROTOCOL_MAB;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.tesla.osgi.provision.PathResolver;
import org.eclipse.tesla.osgi.provision.Storage;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.sonatype.inject.EagerSingleton;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
@EagerSingleton
public class HandlerService
    extends AbstractURLStreamHandlerService
{

    private final Storage storage;

    private final PathResolver pathResolver;

    private final MavenModelResolver modelResolver;

    private final MavenArtifactResolver artifactResolver;

    private final MavenDependencyTreeResolver dependencyTreeResolver;

    @Inject
    HandlerService( final BundleContext bundleContext,
                    final Storage storage,
                    final PathResolver pathResolver,
                    final MavenModelResolver modelResolver,
                    final MavenArtifactResolver artifactResolver,
                    final MavenDependencyTreeResolver dependencyTreeResolver )
    {
        this.storage = storage;
        this.pathResolver = pathResolver;
        this.modelResolver = modelResolver;
        this.artifactResolver = artifactResolver;
        this.dependencyTreeResolver = dependencyTreeResolver;

        final Properties properties = new Properties();
        properties.setProperty( URLConstants.URL_HANDLER_PROTOCOL, PROTOCOL_MAB );
        bundleContext.registerService(
            URLStreamHandlerService.class.getName(),
            this,
            properties
        );
    }

    @Override
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        return new Connection( storage, pathResolver, modelResolver, artifactResolver, dependencyTreeResolver, url );
    }

}
