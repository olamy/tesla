/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.apache.karaf.shell.commands.converter.DefaultConverter;
import org.eclipse.tesla.shell.preparator.validation.ArgumentConversionException;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredArgumentException;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredOptionException;
import org.eclipse.tesla.shell.preparator.validation.MultipleArgumentsWithSameIndexException;
import org.eclipse.tesla.shell.preparator.validation.MultipleOptionsWithSameNameException;
import org.eclipse.tesla.shell.preparator.validation.OptionConversionException;
import org.eclipse.tesla.shell.preparator.validation.TooManyArgumentsException;
import org.eclipse.tesla.shell.preparator.validation.TooManyOptionsException;

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
        return new CommandDescriptor()
            .setScope( command.scope() )
            .setName( command.name() )
            .setDescription( command.description() )
            .setDetailedDescription( command.detailedDescription() );
    }

    @Override
    protected List<OptionDescriptor> getOptionDescriptors( final Action action )
    {
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
                            .setValueToShowInHelp( option.valueToShowInHelp() )
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
                            .setValueToShowInHelp( option.valueToShowInHelp() )
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
                            .setIndex( argument.index() )
                            .setMultiValued( argument.multiValued() )
                            .setRequired( argument.required() )
                            .setValueToShowInHelp( argument.valueToShowInHelp() )
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
                            .setIndex( argument.index() )
                            .setMultiValued( argument.multiValued() )
                            .setRequired( argument.required() )
                            .setValueToShowInHelp( argument.valueToShowInHelp() )
                            .setInjector( new ActionMethodInjector( action, method ) )
                    );
                }
            }
        }
        return arguments;
    }

}
