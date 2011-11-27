package org.eclipse.tesla.shell.command.provision;

import javax.inject.Named;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Command;

/**
 * Alias for "shell:logout".
 *
 * @since 1.0
 */
@Named
@Command( scope = "shell", name = "exit", description = "Exit shell" )
class ExitCommand
    implements Action
{

    @Override
    public Object execute( final CommandSession commandSession )
        throws Exception
    {
        return commandSession.execute( "shell:logout" );
    }

}
