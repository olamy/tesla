/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.sonatype.maven.shell.maven.internal;

import javax.inject.Inject;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.osgi.framework.BundleContext;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class OsgiClassWorld
{

    private BundleContext bundleContext;

    @Inject
    OsgiClassWorld( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public ClassWorld getClassWorld()
    {
        return new ClassWorld( "plexus.core", new OsgiBundleClassLoader( bundleContext.getBundle() ) );
    }

}
