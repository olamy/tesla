package org.eclipse.tesla.shell.preparator.validation;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class MultipleOptionsWithSameNameException
    extends CommandException
{

    public MultipleOptionsWithSameNameException( final CommandDescriptor command,
                                                 final String optionName )
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
                .a( ": there are more options with the same name: " )
                .a( optionName )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format( "There are more options with the same name: %s", optionName )
        );
    }

}
