package org.eclipse.tesla.shell.ai.validation;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.CommandException;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class MultipleArgumentsWithSameIndexException
    extends CommandException
{

    public MultipleArgumentsWithSameIndexException( final Command command, final Argument arg1, final Argument arg2 )
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
                .a( ": arguments " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg1.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " and " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg2.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " have the same index: " )
                .a( arg1.index() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format( "Arguments %s and %s have same the same index: %s", arg1.name(), arg2.name(), arg1.index() )
        );
    }

}
