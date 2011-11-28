/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
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
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class GShellShimShell
    implements Shell
{

    private IO io;

    private final CommandSession commandSession;

    private final Branding branding;

    public GShellShimShell( final CommandSession commandSession )
    {
        this.commandSession = commandSession;
        io = new IO();
        branding = new GShellShimBranding();
    }

    public Branding getBranding()
    {
        return branding;
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
