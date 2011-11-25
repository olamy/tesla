package org.eclipse.tesla.shell.preparator.validation;

import java.lang.reflect.Type;

import org.apache.karaf.shell.commands.CommandException;
import org.apache.karaf.shell.commands.converter.GenericType;
import org.eclipse.tesla.shell.preparator.ArgumentDescriptor;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentConversionException
    extends CommandException
{

    public ArgumentConversionException( final CommandDescriptor command,
                                        final ArgumentDescriptor argument,
                                        final Object value,
                                        final Type expectedType,
                                        final Exception cause )
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
                .a( ": unable to convert argument " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( argument.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " with value '" )
                .a( value )
                .a( "' to type " )
                .a( new GenericType( expectedType ).toString() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format(
                "Unable to convert argument '%s' with value '%s' to type %s",
                argument.getName(), value, new GenericType( expectedType ).toString()
            ),
            cause

        );
    }

}
