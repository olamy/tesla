/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.command.standard;

import javax.inject.Named;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Command;

/**
 * Alias for "shell:logout".
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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
