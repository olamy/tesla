package org.eclipse.tesla.shell.gshell.internal.preparator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.eclipse.tesla.shell.gshell.internal.CommandActionProxy;
import org.eclipse.tesla.shell.gshell.internal.adapter.ArgumentAdapter;
import org.eclipse.tesla.shell.gshell.internal.adapter.OptionAdapter;
import org.eclipse.tesla.shell.gshell.internal.preparator.DefaultActionPreparator;
import org.sonatype.gshell.command.CommandAction;

/**
 * TODO
 *
 * @since 1.0
 */
public class GShellShimActionPreparator
    extends DefaultActionPreparator
{

    private Command commandAnnotation;

    public GShellShimActionPreparator( final Command commandAnnotation )
    {
        this.commandAnnotation = commandAnnotation;
    }

    @Override
    protected void introspect( final Action action,
                               final Map<Option, Injector> options,
                               final Map<Argument, Injector> arguments,
                               final List<Argument> orderedArguments )
    {
        if ( !( action instanceof CommandActionProxy ) )
        {
            super.introspect( action, options, arguments, orderedArguments );
        }
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();
        for ( Class type = commandAction.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( Field field : type.getDeclaredFields() )
            {
                org.sonatype.gshell.util.cli2.Option option =
                    field.getAnnotation( org.sonatype.gshell.util.cli2.Option.class );
                if ( option != null )
                {
                    options.put( new OptionAdapter( option ), new FieldInjector( commandAction, field ) );
                }
                org.sonatype.gshell.util.cli2.Argument argument =
                    field.getAnnotation( org.sonatype.gshell.util.cli2.Argument.class );
                if ( argument != null )
                {
                    final ArgumentAdapter shimArgument = new ArgumentAdapter( argument, field );
                    arguments.put( shimArgument, new FieldInjector( commandAction, field ) );
                    int index = argument.index();
                    while ( orderedArguments.size() <= index )
                    {
                        orderedArguments.add( null );
                    }
                    if ( orderedArguments.get( index ) != null )
                    {
                        throw new IllegalArgumentException( "Duplicate argument index: " + index );
                    }
                    orderedArguments.set( index, shimArgument );
                }
            }
        }
    }

    @Override
    protected Command getCommandAnnotation( final Action action )
    {
        return commandAnnotation;
    }

}
