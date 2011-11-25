package org.eclipse.tesla.shell.preparator.validation;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.eclipse.tesla.shell.preparator.OptionDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class MissingRequiredOptionException
    extends CommandException
{

    public MissingRequiredOptionException( final CommandDescriptor command,
                                           final OptionDescriptor option )
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
                .a( ": option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( option.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " is required" )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Option " + option.getName() + " is required"
        );
    }

}
