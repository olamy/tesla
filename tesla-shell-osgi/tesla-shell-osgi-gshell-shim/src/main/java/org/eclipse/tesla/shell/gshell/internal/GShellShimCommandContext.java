package org.eclipse.tesla.shell.gshell.internal;

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

    public Shell getShell()
    {
        return ShellHolder.get();
    }

    public Object[] getArguments()
    {
        throw new UnsupportedOperationException();
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
