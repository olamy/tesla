package org.eclipse.tesla.shell.ai.validation;

import java.lang.reflect.Type;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.CommandException;
import org.apache.felix.gogo.commands.converter.GenericType;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentConversionException
    extends CommandException
{

    public ArgumentConversionException( final Command command,
                                        final Argument argument,
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
                .a( ": unable to convert argument " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( argument.name() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " with value '" )
                .a( value )
                .a( "' to type " )
                .a( new GenericType( expectedType ).toString() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            "Unable to convert argument " + argument.name() + " with value '"
                + value + "' to type " + new GenericType( expectedType ).toString(),
            cause

        );
    }

}
