package org.eclipse.tesla.shell.preparator.validation;

import java.util.ArrayList;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class TooManyArgumentsException
    extends CommandException
{

    public TooManyArgumentsException( final CommandDescriptor command,
                                      final ArrayList<Object> remainingValues )
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
                .a( ": too many arguments specified " )
                .a( remainingValues.toString() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format( "Too many arguments specified: %s", remainingValues )
        );
    }

}
