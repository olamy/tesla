package org.eclipse.tesla.shell.ai.validation;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.CommandException;
import org.apache.felix.gogo.commands.Option;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class MissingOptionException
    extends CommandException
{

    public MissingOptionException( final Command command, final Option option )
    {
        super(
            Ansi.ansi()
                .fg( Ansi.Color.RED )
                .a( "Error executing command " )
                .a( command.scope() )
                .a( ":" )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( command.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( ": option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( option.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " is required" )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Option " + option.name() + " is required"
        );
    }

}
