package org.eclipse.tesla.shell.command.standard;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.eclipse.tesla.osgi.provision.Provisioner;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Command( scope = "standard", name = "provision", description = "Provision jars" )
class ProvisionCommand
    implements Action
{

    @Argument( name = "coordinates", description = "Maven coordinates of jar to be provisioned", required = true,
               multiValued = true )
    private String[] coordinates;

    @Option( name = "-d", aliases = { "--dryRun" },
             description = "Do not actually install just explain what will be done" )
    private boolean dryRun;

    private final Provisioner provisioner;

    @Inject
    ProvisionCommand( final Provisioner provisioner )
    {
        this.provisioner = provisioner;
    }

    @Override
    public Object execute( final CommandSession commandSession )
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
