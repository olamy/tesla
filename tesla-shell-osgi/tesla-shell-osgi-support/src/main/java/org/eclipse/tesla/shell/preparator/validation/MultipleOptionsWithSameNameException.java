/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator.validation;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * Exception thrown in case that an action implementation has multiple options withs same name / alias.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class MultipleOptionsWithSameNameException
    extends CommandException
{

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public MultipleOptionsWithSameNameException( final CommandDescriptor command,
                                                 final String optionName )
    {
        super(
            Ansi.ansi()
                .fg( Ansi.Color.RED )
                .a( "Error executing command " )
                .a( command.getScope() )
                .a( ":" )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( command.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( ": there are more options with the same name: " )
                .a( optionName )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format( "There are more options with the same name: %s", optionName )
        );
    }

}
