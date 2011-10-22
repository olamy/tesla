package org.eclipse.tesla.shell.support;

import javax.inject.Inject;

import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.BundleContext;

/**
 * TODO
 *
 * @since 1.0
 */
public abstract class GuiceOsgiCommandSupport
    extends OsgiCommandSupport
{

    @Inject
    public GuiceOsgiCommandSupport(){

    }

    @Inject
    public void setBundleContext( final BundleContext bundleContext )
    {
        super.setBundleContext( bundleContext );
    }

}
