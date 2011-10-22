package org.eclipse.tesla.shell.gshell.internal;

import java.io.File;

import org.apache.felix.service.command.CommandSession;
import org.sonatype.gshell.branding.Branding;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.shell.History;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.variables.VariableNames;
import org.sonatype.gshell.variables.Variables;
import org.sonatype.gshell.variables.VariablesImpl;

/**
 * TODO
 *
 * @since 1.0
 */
public class GShellShimShell
    implements Shell
{

    private IO io;

    private final CommandSession commandSession;

    public GShellShimShell( final CommandSession commandSession )
    {
        this.commandSession = commandSession;
        io = new IO();
    }

    public Branding getBranding()
    {
        throw new UnsupportedOperationException();
    }

    public IO getIo()
    {
        return io;
    }

    public Variables getVariables()
    {
        Object variables = commandSession.get( Variables.class.getName() );
        if ( variables == null || !( variables instanceof Variables ) )
        {
            final Variables newVariables = new VariablesImpl();
            commandSession.put( Variables.class.getName(), newVariables );
            newVariables.set( VariableNames.SHELL_HOME, new File( System.getProperty( "shell.home", "." ) ) );
            newVariables.set( VariableNames.SHELL_USER_HOME, new File( System.getProperty( "user.home" ) ) );
            newVariables.set( VariableNames.SHELL_USER_DIR, new File( "." ) );
            variables = newVariables;
        }
        return (Variables) variables;
    }

    public History getHistory()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isOpened()
    {
        return true;
    }

    public void close()
    {
        throw new UnsupportedOperationException();
    }

    public Object execute( final CharSequence charSequence )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public Object execute( final CharSequence charSequence, final Object[] objects )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public Object execute( final Object... objects )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public boolean isInteractive()
    {
        return true;
    }

    public void run( final Object... objects )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

}
