package org.eclipse.tesla.shell.ai.validation;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.CommandException;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class UndefinedOptionException
    extends CommandException
{

    public UndefinedOptionException( final Command command, final Object param )
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
                .a( " undefined option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( param )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Undefined option: " + param
        );
    }

}
