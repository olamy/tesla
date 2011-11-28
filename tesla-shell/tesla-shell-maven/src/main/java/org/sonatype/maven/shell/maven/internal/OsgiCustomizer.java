/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.sonatype.maven.shell.maven.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.framework.BundleContext;
import org.sonatype.maven.shell.maven.MavenRuntimeConfiguration;
import com.google.inject.Injector;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
public class OsgiCustomizer
    implements MavenRuntimeConfiguration.Customizer
{

    private final Injector injector;

    @Inject
    OsgiCustomizer( final Injector injector )
    {
        this.injector = injector;
    }

    public void customize( final MavenRuntimeConfiguration configuration )
    {
        if ( isAnOSGiEnvironment() )
        {
            final OsgiBundleSpaceDelegate spaceDelegate = injector.getInstance( OsgiBundleSpaceDelegate.class );
            configuration.setDelegate( spaceDelegate );

            final OsgiClassWorld classWorldSource = injector.getInstance( OsgiClassWorld.class );
            configuration.setClassWorld( classWorldSource.getClassWorld() );
        }
    }

    private boolean isAnOSGiEnvironment()
    {
        try
        {
            return injector.getBinding( BundleContext.class ) != null;
        }
        catch ( NoClassDefFoundError ignore )
        {
            return false;
        }
    }

}
