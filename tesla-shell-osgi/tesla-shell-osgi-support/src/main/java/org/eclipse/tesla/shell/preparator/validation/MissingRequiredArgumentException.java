package org.eclipse.tesla.shell.preparator.validation;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.ArgumentDescriptor;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class MissingRequiredArgumentException
    extends CommandException
{

    public MissingRequiredArgumentException( final CommandDescriptor command,
                                             final ArgumentDescriptor argument )
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
                .a( ": argument " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( argument.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " is required" )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Argument " + argument.getName() + " is required"
        );
    }

}
