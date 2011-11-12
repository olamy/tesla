package org.eclipse.tesla.osgi.provision.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.utils.log.Logger;
import org.osgi.framework.BundleContext;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class DefaultFelixLogger
    extends Logger
{

    @Inject
    public DefaultFelixLogger( final BundleContext context )
    {
        super( context );
    }

}
