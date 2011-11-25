package org.eclipse.tesla.shell.ai.validation;

import java.lang.reflect.Type;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.CommandException;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.commands.converter.GenericType;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionConversionException
    extends CommandException
{

    public OptionConversionException( final Command command,
                                      final Option option,
                                      final Object value,
                                      final Type expectedType,
                                      final Exception cause )
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
                .a( ": unable to convert option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( option.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " with value '" )
                .a( value )
                .a( "' to type " )
                .a( new GenericType( expectedType ).toString() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Unable to convert option " + option.name() + " with value '"
                + value + "' to type " + new GenericType( expectedType ).toString(),
            cause

        );
    }

}
