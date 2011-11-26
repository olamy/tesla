package org.eclipse.tesla.shell.gshell.internal;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.eclipse.tesla.shell.preparator.AbstractActionPreparator;
import org.eclipse.tesla.shell.preparator.ActionFieldInjector;
import org.eclipse.tesla.shell.preparator.ActionMethodInjector;
import org.eclipse.tesla.shell.preparator.ArgumentDescriptor;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.eclipse.tesla.shell.preparator.OptionDescriptor;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.util.cli2.Argument;
import org.sonatype.gshell.util.cli2.Option;

/**
 * TODO
 *
 * @since 1.0
 */
public class GShellShimActionPreparator
    extends AbstractActionPreparator
    implements ActionPreparator
{

    @Override
    public boolean prepare( final Action action, final CommandSession session, final List<Object> params )
        throws Exception
    {
        checkArgument( action instanceof CommandActionProxy );

        ( (CommandActionProxy) action ).setArguments( params );

        return super.prepare( action, session, params );
    }

    @Override
    protected CommandDescriptor getCommandDescriptor( final Action action )
    {
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();

        final String[] segments = commandAction.getClass().getPackage().getName().split( "\\." );

        return new CommandDescriptor()
            .setScope( segments.length > 0 ? segments[segments.length - 1] : GShellCommandAnnotatedProcessor.SHIM )
            .setName( commandAction.getClass().getAnnotation( Command.class ).name() );
    }

    @Override
    protected List<OptionDescriptor> getOptionDescriptors( final Action action )
    {
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();

        final List<OptionDescriptor> options = new ArrayList<OptionDescriptor>();
        for ( Class type = commandAction.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( final Field field : type.getDeclaredFields() )
            {
                final Option option = field.getAnnotation( Option.class );
                if ( option != null )
                {
                    options.add(
                        optionDescriptor( option )
                            .setInjector( new ActionFieldInjector( commandAction, field ) )
                    );
                }
            }
            for ( final Method method : type.getDeclaredMethods() )
            {
                final Option option = method.getAnnotation( Option.class );
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if ( option != null && parameterTypes != null && parameterTypes.length == 1 )
                {
                    options.add(
                        optionDescriptor( option )
                            .setInjector( new ActionMethodInjector( commandAction, method ) )
                    );
                }
            }
        }
        return options;
    }

    @Override
    protected List<ArgumentDescriptor> getArgumentDescriptors( final Action action )
    {
        final CommandAction commandAction = ( (CommandActionProxy) action ).getCommandAction();

        final List<ArgumentDescriptor> arguments = new ArrayList<ArgumentDescriptor>();
        for ( Class type = commandAction.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( final Field field : type.getDeclaredFields() )
            {
                final Argument argument = field.getAnnotation( Argument.class );
                if ( argument != null )
                {
                    arguments.add(
                        argumentDescriptor( argument, field )
                            .setInjector( new ActionFieldInjector( commandAction, field ) )
                    );
                }
            }
            for ( final Method method : type.getDeclaredMethods() )
            {
                final Argument argument = method.getAnnotation( Argument.class );
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if ( argument != null && parameterTypes != null && parameterTypes.length == 1 )
                {
                    arguments.add(
                        argumentDescriptor( argument, method )
                            .setInjector( new ActionMethodInjector( commandAction, method ) )
                    );
                }
            }
        }
        return arguments;
    }

    private ArgumentDescriptor argumentDescriptor( final Argument argument, final Field field )
    {
        return argumentDescriptor( argument )
            .setName( field.getName() )
            .setMultiValued( field.getType().isArray() || Collection.class.isAssignableFrom( field.getType() ) );
    }

    private ArgumentDescriptor argumentDescriptor( final Argument argument, final Method method )
    {
        return argumentDescriptor( argument )
            .setName( method.getName() )
            .setMultiValued( method.getParameterTypes()[0].isArray()
                                 || Collection.class.isAssignableFrom( method.getParameterTypes()[0] ) );
    }

    private ArgumentDescriptor argumentDescriptor( final Argument argument )
    {
        return new ArgumentDescriptor()
            .setDescription( argument.description() )
            .setIndex( argument.index() )
            .setRequired( argument.required() );
    }

    private OptionDescriptor optionDescriptor( final Option option )
    {
        final OptionDescriptor optionDescriptor = new OptionDescriptor();
        if ( option.name() == null || "__EMPTY__".equals( option.name() ) )
        {
            optionDescriptor.setName( "--" + option.longName() );
        }
        else
        {
            optionDescriptor.setName( "-" + option.name() );
        }
        if ( !( option.name() == null || "__EMPTY__".equals( option.name() )
            || option.longName() == null || "__EMPTY__".equals( option.longName() ) ) )
        {
            optionDescriptor.setAliases( "--" + option.longName() );
        }
        optionDescriptor.setDescription( option.description() );
        optionDescriptor.setRequired( option.required() );
        return optionDescriptor;
    }

}
