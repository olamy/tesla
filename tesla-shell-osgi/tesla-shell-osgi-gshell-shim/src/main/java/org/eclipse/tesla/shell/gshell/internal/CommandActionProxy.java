package org.eclipse.tesla.shell.gshell.internal;

import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.io.StreamJack;

/**
 * TODO
 *
 * @since 1.0
 */
public class CommandActionProxy
    implements Action
{

    private final CommandAction commandAction;

    private List<Object> arguments;

    CommandActionProxy( final CommandAction commandAction )
    {
        this.commandAction = commandAction;
    }

    public Object execute( final CommandSession commandSession )
        throws Exception
    {
        try
        {
            StreamJack.install();
            ShellHolder.set( new GShellShimShell( commandSession ) );
            commandAction.execute( new GShellShimCommandContext( arguments ) );
            return null;
        }
        finally
        {
            StreamJack.restore();
        }
    }

    public CommandAction getCommandAction()
    {
        return commandAction;
    }

    public void setArguments( final List<Object> arguments )
    {
        this.arguments = arguments;
    }
}
