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

import static java.util.ResourceBundle.getBundle;

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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
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

public abstract class AbstractActionPreparator
    implements ActionPreparator
{

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    protected AbstractActionPreparator()
    {
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public boolean prepare( final Action action, final CommandSession session, final List<Object> params )
        throws Exception
    {
        // Introspect action for extraction of command
        final CommandDescriptor commandDescriptor = getCommandDescriptor( action );

        // Introspect action for extraction of arguments
        final List<ArgumentDescriptor> argumentDescriptors =
            new ArrayList<ArgumentDescriptor>( getArgumentDescriptors( action ) );
        // sort arguments by index and ensure that index is unique
        sortArgumentDescriptors( commandDescriptor, argumentDescriptors );

        // Introspect action for extraction of options
        final List<OptionDescriptor> optionDescriptors = getOptionDescriptors( action );

        if ( isHelp( params ) )
        {
            UsagePrinter.print(
                session, action, commandDescriptor, optionDescriptors, argumentDescriptors, System.out
            );
            return false;
        }

        // start considering all params as arguments
        final List<Object> argumentValues = new ArrayList<Object>( params == null ? Collections.emptyList() : params );
        // then extract options out
        final Map<String, Object> optionValues = extractOptions( findSwitches( optionDescriptors ), argumentValues );

        injectOptions( action, commandDescriptor, optionDescriptors, optionValues );
        injectArguments( action, commandDescriptor, argumentDescriptors, argumentValues );

        return true;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    protected abstract CommandDescriptor getCommandDescriptor( final Action action );

    protected abstract List<OptionDescriptor> getOptionDescriptors( final Action action );

    protected abstract List<ArgumentDescriptor> getArgumentDescriptors( final Action action );

    protected ResourceBundle loadResourceBundle( final Class<?> clazz )
    {
        ResourceBundle resourceBundle = null;
        try
        {
            resourceBundle = getBundle( clazz.getName(), Locale.getDefault(), clazz.getClassLoader() );
        }
        catch ( Exception ignore )
        {
            // ignore
        }
        return resourceBundle;
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private List<String> findSwitches( final List<OptionDescriptor> descriptors )
    {
        final ArrayList<String> switches = new ArrayList<String>();
        for ( final OptionDescriptor descriptor : descriptors )
        {
            final Class<?> optionType = descriptor.getInjector().getType();
            if ( optionType == boolean.class || optionType == Boolean.class )
            {
                switches.add( descriptor.getName() );
                if ( descriptor.getAliases() != null )
                {
                    for ( final String alias : descriptor.getAliases() )
                    {
                        if ( alias != null && alias.trim().length() > 0 )
                        {
                            switches.add( alias );
                        }
                    }
                }
            }
        }
        return switches;
    }

    private void injectArguments( final Action action,
                                  final CommandDescriptor commandDescriptor,
                                  final List<ArgumentDescriptor> argumentDescriptors,
                                  final List<Object> argumentValues )
        throws Exception
    {
        final ArrayList<Object> remainingValues = new ArrayList<Object>( argumentValues );
        for ( ArgumentDescriptor descriptor : argumentDescriptors )
        {
            final ActionInjector injector = descriptor.getInjector();
            if ( remainingValues.size() > 0 )
            {
                Object value;
                if ( descriptor.isMultiValued() )
                {
                    value = new ArrayList<Object>( remainingValues );
                    remainingValues.clear();
                }
                else
                {
                    value = remainingValues.remove( 0 );
                }
                try
                {
                    final Object converted = convert(
                        action, value, injector.getGenericType(), descriptor.isMultiValued()
                    );
                    injector.set( converted );
                }
                catch ( Exception e )
                {
                    throw new ArgumentConversionException(
                        commandDescriptor, descriptor, value, injector.getGenericType(), e
                    );
                }
            }
            else if ( descriptor.isRequired() )
            {
                throw new MissingRequiredArgumentException( commandDescriptor, descriptor );
            }
        }
        if ( remainingValues.size() > 0 )
        {
            throw new TooManyArgumentsException( commandDescriptor, remainingValues );
        }
    }

    private void injectOptions( final Action action,
                                final CommandDescriptor commandDescriptor,
                                final List<OptionDescriptor> options,
                                final Map<String, Object> optionValues )
        throws Exception
    {
        final Map<String, OptionDescriptor> nameToDescriptor = mapOptionsByName( commandDescriptor, options );
        final Set<OptionDescriptor> remainingOptions = new HashSet<OptionDescriptor>( options );

        for ( Map.Entry<String, Object> entry : optionValues.entrySet() )
        {
            final OptionDescriptor descriptor = nameToDescriptor.get( entry.getKey() );
            if ( descriptor == null )
            {
                throw new TooManyOptionsException( commandDescriptor, entry.getKey() );
            }
            remainingOptions.remove( descriptor );
            final ActionInjector injector = descriptor.getInjector();
            try
            {
                final Object converted = convert(
                    action, entry.getValue(), injector.getGenericType(), descriptor.isMultiValued()
                );
                injector.set( converted );
            }
            catch ( Exception e )
            {
                throw new OptionConversionException(
                    commandDescriptor, descriptor, entry.getValue(), injector.getGenericType(), e
                );
            }
        }
        if ( remainingOptions.size() > 0 )
        {
            for ( final OptionDescriptor descriptor : remainingOptions )
            {
                if ( descriptor.isRequired() )
                {
                    throw new MissingRequiredOptionException( commandDescriptor, descriptor );
                }
            }
        }
    }

    private Map<String, OptionDescriptor> mapOptionsByName( final CommandDescriptor commandDescriptor,
                                                            final List<OptionDescriptor> descriptors )
        throws Exception
    {
        final Map<String, OptionDescriptor> nameToBinding = new HashMap<String, OptionDescriptor>();
        for ( final OptionDescriptor descriptor : descriptors )
        {
            if ( nameToBinding.put( descriptor.getName(), descriptor ) != null )
            {
                throw new MultipleOptionsWithSameNameException( commandDescriptor, descriptor.getName() );
            }
            if ( descriptor.getAliases() != null )
            {
                for ( final String alias : descriptor.getAliases() )
                {
                    if ( alias != null && alias.trim().length() > 0 )
                    {
                        if ( nameToBinding.put( alias, descriptor ) != null )
                        {
                            throw new MultipleOptionsWithSameNameException( commandDescriptor, alias );
                        }
                    }
                }
            }
        }
        return nameToBinding;
    }

    private Map<String, Object> extractOptions( final List<String> switchesNames, final List<Object> argumentValues )
    {
        final Map<String, Object> optionValues = new HashMap<String, Object>();
        final List<Object> copyOfArgumentValues = new ArrayList<Object>( argumentValues );
        int i = -1;
        while ( i < copyOfArgumentValues.size() - 1 )
        {
            i++;

            final Object value = copyOfArgumentValues.get( i );
            if ( value instanceof String )
            {
                final String valueAsString = (String) value;

                // do we have an option?
                if ( valueAsString.startsWith( "-" ) )
                {
                    // we have an option name, remove it from arguments list
                    removeByIdentity( argumentValues, value );

                    // everything after "--" is an argument
                    if ( valueAsString.equals( "--" ) )
                    {
                        break;
                    }

                    // look ahead
                    if ( i < copyOfArgumentValues.size() - 1 )
                    {
                        final Object lookAheadValue = copyOfArgumentValues.get( i + 1 );
                        if ( lookAheadValue instanceof String )
                        {
                            final String lookAheadAsString = (String) lookAheadValue;

                            if ( switchesNames.contains( valueAsString ) )
                            {
                                if ( !"true".equalsIgnoreCase( lookAheadAsString )
                                    && !"false".equalsIgnoreCase( lookAheadAsString )
                                    && ( lookAheadAsString.startsWith( "-" )
                                    || lookAheadAsString.equals( "--" )
                                    || !Boolean.valueOf( lookAheadAsString ) ) )
                                {
                                    setOption( optionValues, valueAsString, true );
                                    continue;
                                }
                            }
                        }
                        // we have a value for the option, remove it from arguments list
                        removeByIdentity( argumentValues, lookAheadValue );
                        // and add it as an option
                        setOption( optionValues, valueAsString, lookAheadValue );
                    }
                    else
                    {
                        setOption( optionValues, valueAsString, switchesNames.contains( valueAsString ) ? true : null );
                    }
                }
            }
        }

        return optionValues;
    }

    @SuppressWarnings( "unchecked" )
    private void setOption( final Map<String, Object> optionValues, final String name, final Object value )
    {
        final Object currentValue = optionValues.get( name );
        if ( currentValue != null )
        {
            if ( currentValue instanceof List )
            {
                ( (List) currentValue ).add( value );
            }
            else
            {
                optionValues.put( name, new ArrayList<Object>( Arrays.asList( currentValue, value ) ) );
            }
        }
        else
        {
            optionValues.put( name, value );
        }
    }

    private void removeByIdentity( final List<Object> list, final Object toRemove )
    {
        if ( list != null && list.size() > 0 )
        {
            final Iterator<Object> iterator = list.iterator();
            while ( iterator.hasNext() )
            {
                if ( iterator.next() == toRemove )
                {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private void sortArgumentDescriptors( final CommandDescriptor commandDescriptor,
                                          final List<ArgumentDescriptor> arguments )
        throws Exception
    {
        try
        {
            Collections.sort( arguments, new Comparator<ArgumentDescriptor>()
            {
                @Override
                public int compare( final ArgumentDescriptor ad1, final ArgumentDescriptor ad2 )
                {
                    final int result = Integer.valueOf( ad1.getIndex() ).compareTo( ad2.getIndex() );
                    if ( result == 0 )
                    {
                        throw new IllegalArgumentException(
                            new MultipleArgumentsWithSameIndexException(
                                commandDescriptor, ad1, ad2
                            )
                        );
                    }
                    return result;
                }
            } );
        }
        catch ( IllegalArgumentException e )
        {
            throw (MultipleArgumentsWithSameIndexException) e.getCause();
        }
    }

    private boolean isHelp( final List<Object> params )
    {
        if ( params != null )
        {
            for ( final Object param : params )
            {
                if ( "--help".equals( param ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected Object convert( final Action action,
                              final Object value,
                              final Type toType,
                              final boolean multiValue )
        throws Exception
    {
        if ( toType == String.class )
        {
            return value != null ? value.toString() : null;
        }
        Object toConvert = value;
        if ( multiValue && !( toConvert.getClass().isArray() || toConvert instanceof Collection ) )
        {
            toConvert = new Object[]{ toConvert };
        }
        return new DefaultConverter( action.getClass().getClassLoader() ).convert( toConvert, toType );
    }

}
