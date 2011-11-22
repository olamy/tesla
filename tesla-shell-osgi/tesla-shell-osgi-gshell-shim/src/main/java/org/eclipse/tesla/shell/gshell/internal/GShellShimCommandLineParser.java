package org.eclipse.tesla.shell.gshell.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.CommandSession;
import org.eclipse.tesla.shell.ai.ActionFieldInjector;
import org.eclipse.tesla.shell.ai.ActionMethodInjector;
import org.eclipse.tesla.shell.ai.ArgumentBinding;
import org.eclipse.tesla.shell.ai.CommandLineParser;
import org.eclipse.tesla.shell.ai.OptionBinding;
import org.eclipse.tesla.shell.gshell.internal.adapter.FieldArgumentAdapter;
import org.eclipse.tesla.shell.gshell.internal.adapter.MethodArgumentAdapter;
import org.eclipse.tesla.shell.gshell.internal.adapter.OptionAdapter;
import org.sonatype.gshell.command.CommandAction;

/**
 * TODO
 *
 * @since 1.0
 */
public class GShellShimCommandLineParser
    extends CommandLineParser
{

    private Command commandAnnotation;

    public GShellShimCommandLineParser( final Command commandAnnotation )
    {
        this.commandAnnotation = commandAnnotation;
    }

    @Override
    public boolean prepare( final Action action, final CommandSession session, final List<Object> params )
        throws Exception
    {
        if ( action instanceof CommandActionProxy )
        {
            ( (CommandActionProxy) action ).setArguments( params );
        }
        return super.prepare( action, session, params );
    }

    @Override
    protected List<OptionBinding> getOptions( final Action action )
    {
        if ( !( action instanceof CommandActionProxy ) )
        {
            return super.getOptions( action );
        }

        final List<OptionBinding> options = new ArrayList<OptionBinding>();
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();
        for ( Class type = commandAction.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( Field field : type.getDeclaredFields() )
            {
                final org.sonatype.gshell.util.cli2.Option option = field.getAnnotation(
                    org.sonatype.gshell.util.cli2.Option.class
                );
                if ( option != null )
                {
                    options.add( new OptionBinding(
                        new OptionAdapter( option ), new ActionFieldInjector( commandAction, field ) )
                    );
                }
            }
            for ( Method method : type.getDeclaredMethods() )
            {
                final org.sonatype.gshell.util.cli2.Option option = method.getAnnotation(
                    org.sonatype.gshell.util.cli2.Option.class
                );
                if ( option != null )
                {
                    options.add( new OptionBinding(
                        new OptionAdapter( option ), new ActionMethodInjector( commandAction, method ) )
                    );
                }
            }
        }
        return options;
    }

    @Override
    protected List<ArgumentBinding> getArguments( final Action action )
    {
        if ( !( action instanceof CommandActionProxy ) )
        {
            return super.getArguments( action );
        }

        final List<ArgumentBinding> arguments = new ArrayList<ArgumentBinding>();
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();
        for ( Class type = commandAction.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( Field field : type.getDeclaredFields() )
            {
                final org.sonatype.gshell.util.cli2.Argument argument = field.getAnnotation(
                    org.sonatype.gshell.util.cli2.Argument.class
                );
                if ( argument != null )
                {
                    final FieldArgumentAdapter shimArgument = new FieldArgumentAdapter( argument, field );
                    arguments.add( new ArgumentBinding(
                        shimArgument, new ActionFieldInjector( commandAction, field ) )
                    );
                }
            }
            for ( Method method : type.getDeclaredMethods() )
            {
                final org.sonatype.gshell.util.cli2.Argument argument = method.getAnnotation(
                    org.sonatype.gshell.util.cli2.Argument.class
                );
                if ( argument != null )
                {
                    final MethodArgumentAdapter shimArgument = new MethodArgumentAdapter( argument, method );
                    arguments.add( new ArgumentBinding(
                        shimArgument, new ActionMethodInjector( commandAction, method ) )
                    );
                }
            }
        }
        return arguments;
    }

    @Override
    protected Command getCommand( final Action action )
    {
        return commandAnnotation;
    }

}
