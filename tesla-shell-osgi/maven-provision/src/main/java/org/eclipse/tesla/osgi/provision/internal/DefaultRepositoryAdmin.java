package org.eclipse.tesla.osgi.provision.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.osgi.framework.BundleContext;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class DefaultRepositoryAdmin
    extends RepositoryAdminImpl
{

    @Inject
    public DefaultRepositoryAdmin( final BundleContext context,
                                   final Logger logger )
    {
        super( context, logger );
    }

}
