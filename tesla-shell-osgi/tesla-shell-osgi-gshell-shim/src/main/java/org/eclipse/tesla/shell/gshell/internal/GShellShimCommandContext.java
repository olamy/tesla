package org.eclipse.tesla.shell.gshell.internal;

import java.util.Collections;
import java.util.List;

import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.variables.Variables;

/**
 * TODO
 *
 * @since 1.0
 */
class GShellShimCommandContext
    implements CommandContext
{

    private final List<Object> arguments;

    public GShellShimCommandContext( final List<Object> arguments )
    {
        this.arguments = arguments == null ? Collections.emptyList() : arguments;
    }

    public Shell getShell()
    {
        return ShellHolder.get();
    }

    public Object[] getArguments()
    {
        return arguments.toArray( new Object[arguments.size()] );
    }

    public IO getIo()
    {
        return getShell().getIo();
    }

    public Variables getVariables()
    {
        return getShell().getVariables();
    }
}
