/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal;

import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.io.StreamJack;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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

    public Object execute( final CommandSession session )
        throws Exception
    {
        try
        {
            StreamJack.install();
            ShellHolder.set( new GShellShimShell( session ) );
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
