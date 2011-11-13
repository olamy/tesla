package org.eclipse.tesla.shell.command.provision;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.eclipse.tesla.osgi.provision.Provisioner;
import org.eclipse.tesla.shell.support.GuiceOsgiCommandSupport;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Command( scope = "provision", name = "install", description = "Provision jars" )
public class ProvisionCommand
    extends GuiceOsgiCommandSupport
{

    @Argument( name = "coordinates", description = "Maven coordinates of jar to be provisioned", required = true,
               multiValued = true )
    private String[] coordinates;

    @Option( name = "-d", aliases = { "--dryRun" },
             description = "Do not actually install just explain what will be done" )
    private boolean dryRun;

    @Inject
    private Provisioner provisioner;

    @Override
    protected Object doExecute()
        throws Exception
    {
        final ProvisionSet provisionSet = provisioner.resolve( coordinates );
        if ( provisionSet.hasProblems() )
        {
            provisionSet.printProblems( System.err );
        }
        else
        {
            provisionSet.installAndStart();
        }
        return null;
    }

}
