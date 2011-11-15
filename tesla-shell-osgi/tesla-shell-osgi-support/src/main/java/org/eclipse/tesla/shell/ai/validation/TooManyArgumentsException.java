package org.eclipse.tesla.shell.ai.validation;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.CommandException;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class TooManyArgumentsException
    extends CommandException
{

    public TooManyArgumentsException( final Command command )
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
                .a( ": too many arguments specified" )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Too many arguments specified"
        );
    }

}
