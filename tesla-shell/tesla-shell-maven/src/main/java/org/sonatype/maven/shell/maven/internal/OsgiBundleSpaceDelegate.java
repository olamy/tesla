/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.sonatype.maven.shell.maven.internal;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.sonatype.guice.bean.reflect.BundleClassSpace;
import org.sonatype.guice.plexus.binders.PlexusXmlBeanModule;
import org.sonatype.guice.plexus.config.PlexusBeanModule;
import org.sonatype.maven.shell.maven.MavenRuntimeConfiguration;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class OsgiBundleSpaceDelegate
    implements MavenRuntimeConfiguration.Delegate
{

    private BundleContext bundleContext;

    @Inject
    OsgiBundleSpaceDelegate( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void configure( final DefaultPlexusContainer container )
        throws Exception
    {
        for ( final Bundle bundle : bundleContext.getBundles() )
        {
            if ( bundle.getBundleId() != 0 )
            {
                final List<PlexusBeanModule> beanModules = new ArrayList<PlexusBeanModule>();
                final ContextMapAdapter variables = new ContextMapAdapter( container.getContext() );
                final BundleClassSpace space = new BundleClassSpace( bundle );
                beanModules.add( new PlexusXmlBeanModule( space, variables ) );
                container.addPlexusInjector( beanModules );
            }
        }
    }

}
