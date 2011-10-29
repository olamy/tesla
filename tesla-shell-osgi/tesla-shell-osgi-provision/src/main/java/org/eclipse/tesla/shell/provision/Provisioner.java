package org.eclipse.tesla.shell.provision;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Provisioner
{

    void provision(String... coordinates);

    void dryRun(String... coordinates);

}
