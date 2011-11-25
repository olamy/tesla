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
public class MultipleArgumentsWithSameIndexException
    extends CommandException
{

    public MultipleArgumentsWithSameIndexException( final CommandDescriptor command,
                                                    final ArgumentDescriptor arg1,
                                                    final ArgumentDescriptor arg2 )
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
                .a( ": arguments " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg1.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " and " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg2.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " have the same index: " )
                .a( arg1.getIndex() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format(
                "Arguments %s and %s have same the same index: %s",
                arg1.getName(), arg2.getName(), arg1.getIndex()
            )
        );
    }

}
