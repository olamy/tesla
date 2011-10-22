package org.eclipse.tesla.shell.gshell.internal;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.service.command.CommandSession;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;

/**
 * TODO
 *
 * @since 1.0
 */
class GShellShimAction
    implements Action
{

    private final CommandAction commandAction;

    GShellShimAction( final CommandAction commandAction )
    {
        this.commandAction = commandAction;
    }

    public Object execute( final CommandSession commandSession )
        throws Exception
    {
        ShellHolder.set( new GShellShimShell(commandSession) );
        commandAction.execute( new GShellShimCommandContext() );
        return null;
    }

    CommandAction getCommandAction()
    {
        return commandAction;
    }

}
