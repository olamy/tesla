/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal;

import java.util.Collections;
import java.util.List;

import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.variables.Variables;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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
