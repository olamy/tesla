/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.commands.basic.ActionPreparator;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class DefaultActionPreparator
    extends AbstractActionPreparator
    implements ActionPreparator
{

    public static final DefaultActionPreparator INSTANCE = new DefaultActionPreparator();

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    protected DefaultActionPreparator()
    {
        // just to almost force using of singleton instance
    }

    @Override
    protected CommandDescriptor getCommandDescriptor( final Action action )
    {
        final Command command = action.getClass().getAnnotation( Command.class );
        final ResourceBundle resourceBundle = loadResourceBundle( action.getClass() );
        return new CommandDescriptor()
            .setScope( command.scope() )
            .setName( command.name() )
            .setDescription( command.description() )
            .loadDescription( resourceBundle )
            .setDetailedDescription( command.detailedDescription() )
            .loadDetailedDescription( resourceBundle );
    }

    @Override
    protected List<OptionDescriptor> getOptionDescriptors( final Action action )
    {
        final ResourceBundle resourceBundle = loadResourceBundle( action.getClass() );
        final List<OptionDescriptor> descriptors = new ArrayList<OptionDescriptor>();
        for ( Class type = action.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( final Field field : type.getDeclaredFields() )
            {
                final Option option = field.getAnnotation( Option.class );
                if ( option != null )
                {
                    descriptors.add(
                        new OptionDescriptor()
                            .setName( option.name() )
                            .setAliases( option.aliases() )
                            .setMultiValued( option.multiValued() )
                            .setRequired( option.required() )
                            .setDescription( option.description() )
                            .loadDescription( resourceBundle, field.getName() )
                            .setValueToShowInHelp( option.valueToShowInHelp() )
                            .loadValueToShowInHelp( resourceBundle, field.getName() )
                            .setInjector( new ActionFieldInjector( action, field ) )
                    );
                }
            }
            for ( final Method method : type.getDeclaredMethods() )
            {
                final Option option = method.getAnnotation( Option.class );
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if ( option != null && parameterTypes != null && parameterTypes.length == 1 )
                {
                    descriptors.add(
                        new OptionDescriptor()
                            .setName( option.name() )
                            .setAliases( option.aliases() )
                            .setMultiValued( option.multiValued() )
                            .setRequired( option.required() )
                            .setDescription( option.description() )
                            .loadDescription( resourceBundle, method.getName() )
                            .setValueToShowInHelp( option.valueToShowInHelp() )
                            .loadValueToShowInHelp( resourceBundle, method.getName() )
                            .setInjector( new ActionMethodInjector( action, method ) )
                    );
                }
            }
        }
        return descriptors;
    }

    @Override
    protected List<ArgumentDescriptor> getArgumentDescriptors( final Action action )
    {
        final ResourceBundle resourceBundle = loadResourceBundle( action.getClass() );
        final List<ArgumentDescriptor> arguments = new ArrayList<ArgumentDescriptor>();
        for ( Class type = action.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( final Field field : type.getDeclaredFields() )
            {
                final Argument argument = field.getAnnotation( Argument.class );
                if ( argument != null )
                {
                    arguments.add(
                        new ArgumentDescriptor()
                            .setName( Argument.DEFAULT.equals( argument.name() ) ? field.getName() : argument.name() )
                            .setDescription( argument.description() )
                            .loadDescription( resourceBundle, field.getName() )
                            .setIndex( argument.index() )
                            .setMultiValued( argument.multiValued() )
                            .setRequired( argument.required() )
                            .setValueToShowInHelp( argument.valueToShowInHelp() )
                            .loadValueToShowInHelp( resourceBundle, field.getName() )
                            .setInjector( new ActionFieldInjector( action, field ) )
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
                        new ArgumentDescriptor()
                            .setName( Argument.DEFAULT.equals( argument.name() ) ? method.getName() : argument.name() )
                            .setDescription( argument.description() )
                            .loadDescription( resourceBundle, method.getName() )
                            .setIndex( argument.index() )
                            .setMultiValued( argument.multiValued() )
                            .setRequired( argument.required() )
                            .setValueToShowInHelp( argument.valueToShowInHelp() )
                            .loadValueToShowInHelp( resourceBundle, method.getName() )
                            .setInjector( new ActionMethodInjector( action, method ) )
                    );
                }
            }
        }
        return arguments;
    }

}
