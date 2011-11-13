package org.eclipse.tesla.osgi.provision;

import org.osgi.framework.Bundle;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Provisioner
{

    ProvisionSet resolve( String... coordinates );

}
