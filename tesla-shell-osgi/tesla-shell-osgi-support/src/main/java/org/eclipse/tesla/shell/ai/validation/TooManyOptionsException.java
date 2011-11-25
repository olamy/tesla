package org.eclipse.tesla.shell.ai.validation;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.CommandException;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class TooManyOptionsException
    extends CommandException
{

    public TooManyOptionsException( final Command command, final String name )
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
                .a( name )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Undefined option: " + name
        );
    }

}
