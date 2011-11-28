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
 * Exception thrown in case that there are more options then expected.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class TooManyOptionsException
    extends CommandException
{

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public TooManyOptionsException( final CommandDescriptor command,
                                    final String name )
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
                .a( " undefined option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( name )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Undefined option: " + name
        );
    }

}
