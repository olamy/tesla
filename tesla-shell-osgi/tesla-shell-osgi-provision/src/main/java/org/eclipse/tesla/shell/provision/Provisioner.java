package org.eclipse.tesla.shell.provision;

import org.osgi.framework.Bundle;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Provisioner
{

    Bundle[] install( String... coordinates );

    Bundle[] installAndStart( String... coordinates );

    void dryRun( String... coordinates );

}
