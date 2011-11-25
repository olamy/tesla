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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.apache.karaf.shell.commands.converter.DefaultConverter;
import org.apache.karaf.shell.console.NameScoping;
import org.eclipse.tesla.shell.preparator.validation.ArgumentConversionException;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredArgumentException;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredOptionException;
import org.eclipse.tesla.shell.preparator.validation.MultipleArgumentsWithSameIndexException;
import org.eclipse.tesla.shell.preparator.validation.MultipleOptionsWithSameNameException;
import org.eclipse.tesla.shell.preparator.validation.OptionConversionException;
import org.eclipse.tesla.shell.preparator.validation.TooManyArgumentsException;
import org.eclipse.tesla.shell.preparator.validation.TooManyOptionsException;
import org.fusesource.jansi.Ansi;
import jline.Terminal;

public class DefaultActionPreparator
    implements ActionPreparator
{

    public static final DefaultActionPreparator INSTANCE = new DefaultActionPreparator();

    protected DefaultActionPreparator()
    {
        // just to almost force using of singleton instance
    }

    public boolean prepare( final Action action, final CommandSession session, final List<Object> params )
        throws Exception
    {
        // Introspect action for extraction of command
        final CommandDescriptor commandDescriptor = getCommandDescriptor( action );

        // Introspect action for extraction of arguments
        final List<ArgumentDescriptor> arguments =
            new ArrayList<ArgumentDescriptor>( getArgumentDescriptors( action ) );
        // sort arguments by index and ensure that index is unique
        sortArgumentDescriptors( commandDescriptor, arguments );

        // Introspect action for extraction of options
        final List<OptionDescriptor> options = getOptionDescriptors( action );

        if ( isHelp( params ) )
        {
            printUsage( session, action, options, arguments, System.out );
            return false;
        }

        // start considering all params as arguments
        final List<Object> argumentValues = new ArrayList<Object>( params == null ? Collections.emptyList() : params );
        // then extract options out
        final Map<String, Object> optionValues = extractOptions( findSwitches( options ), argumentValues );

        injectOptions( action, commandDescriptor, options, optionValues );
        injectArguments( action, commandDescriptor, arguments, argumentValues );

        return true;
    }

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

    protected CommandDescriptor getCommandDescriptor( final Action action )
    {
        final Command command = action.getClass().getAnnotation( Command.class );
        return new CommandDescriptor()
            .setScope( command.scope() )
            .setName( command.name() )
            .setDescription( command.description() )
            .setDetailedDescription( command.detailedDescription() );
    }

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

    private void printUsage( final CommandSession session,
                             final Action action,
                             final List<OptionDescriptor> options,
                             final List<ArgumentDescriptor> argumentDescriptors,
                             final PrintStream out )
    {
        options.add(
            new OptionDescriptor()
                .setName( "-h" )
                .setAliases( "--help" )
                .setDescription( "Display this help message" )
        );
        CommandDescriptor commandDescriptor = getCommandDescriptor( action );
        Terminal term = session != null ? (Terminal) session.get( ".jline.terminal" ) : null;

        boolean globalScope = NameScoping.isGlobalScope( session, commandDescriptor.getScope() );
        if ( commandDescriptor != null
            && ( commandDescriptor.getDescription() != null || commandDescriptor.getName() != null ) )
        {
            out.println( Ansi.ansi().a( Ansi.Attribute.INTENSITY_BOLD ).a( "DESCRIPTION" ).a( Ansi.Attribute.RESET ) );
            out.print( "        " );
            if ( commandDescriptor.getName() != null )
            {
                if ( globalScope )
                {
                    out.println(
                        Ansi.ansi()
                            .a( Ansi.Attribute.INTENSITY_BOLD )
                            .a( commandDescriptor.getName() )
                            .a( Ansi.Attribute.RESET )
                    );
                }
                else
                {
                    out.println(
                        Ansi.ansi()
                            .a( commandDescriptor.getScope() )
                            .a( ":" ).a( Ansi.Attribute.INTENSITY_BOLD )
                            .a( commandDescriptor.getName() )
                            .a( Ansi.Attribute.RESET )
                    );
                }
                out.println();
            }
            out.print( "\t" );
            out.println( commandDescriptor.getDescription() );
            out.println();
        }
        StringBuilder syntax = new StringBuilder();
        if ( commandDescriptor != null )
        {
            if ( globalScope )
            {
                syntax.append( commandDescriptor.getName() );
            }
            else
            {
                syntax.append( String.format( "%s:%s", commandDescriptor.getScope(), commandDescriptor.getName() ) );
            }
        }
        if ( options.size() > 0 )
        {
            syntax.append( " [options]" );
        }
        if ( argumentDescriptors.size() > 0 )
        {
            syntax.append( ' ' );
            for ( ArgumentDescriptor descriptor : argumentDescriptors )
            {
                if ( !descriptor.isRequired() )
                {
                    syntax.append( String.format( "[%s] ", descriptor.getName() ) );
                }
                else
                {
                    syntax.append( String.format( "%s ", descriptor.getName() ) );
                }
            }
        }

        out.println(
            Ansi.ansi()
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( "SYNTAX" )
                .a( Ansi.Attribute.RESET )
        );
        out.print( "        " );
        out.println( syntax.toString() );
        out.println();
        if ( argumentDescriptors.size() > 0 )
        {
            out.println(
                Ansi.ansi()
                    .a( Ansi.Attribute.INTENSITY_BOLD )
                    .a( "ARGUMENTS" )
                    .a( Ansi.Attribute.RESET )
            );
            for ( ArgumentDescriptor descriptor : argumentDescriptors )
            {
                out.print( "        " );
                out.println(
                    Ansi.ansi()
                        .a( Ansi.Attribute.INTENSITY_BOLD )
                        .a( descriptor.getName() )
                        .a( Ansi.Attribute.RESET )
                );
                printFormatted(
                    "                ", descriptor.getDescription(), term != null ? term.getWidth() : 80, out
                );
                if ( !descriptor.isRequired() )
                {
                    if ( descriptor.getValueToShowInHelp() != null && descriptor.getValueToShowInHelp().length() != 0 )
                    {
                        try
                        {
                            if ( ArgumentDescriptor.DEFAULT.equals( descriptor.getValueToShowInHelp() ) )
                            {
                                Object o = descriptor.getInjector().get();
                                printObjectDefaultsTo( out, o );
                            }
                            else
                            {
                                printDefaultsTo( out, descriptor.getValueToShowInHelp() );
                            }
                        }
                        catch ( Throwable t )
                        {
                            // Ignore
                        }
                    }
                }
            }
            out.println();
        }
        if ( options.size() > 0 )
        {
            out.println( Ansi.ansi().a( Ansi.Attribute.INTENSITY_BOLD ).a( "OPTIONS" ).a( Ansi.Attribute.RESET ) );
            for ( OptionDescriptor descriptor : options )
            {
                String opt = descriptor.getName();
                for ( final String alias : descriptor.getAliases() )
                {
                    opt += ", " + alias;
                }
                out.print( "        " );
                out.println( Ansi.ansi().a( Ansi.Attribute.INTENSITY_BOLD ).a( opt ).a( Ansi.Attribute.RESET ) );
                printFormatted(
                    "                ", descriptor.getDescription(), term != null ? term.getWidth() : 80, out
                );
                if ( descriptor.getValueToShowInHelp() != null && descriptor.getValueToShowInHelp().length() != 0 )
                {
                    try
                    {
                        if ( OptionDescriptor.DEFAULT.equals( descriptor.getValueToShowInHelp() ) )
                        {
                            Object o = descriptor.getInjector().get();
                            printObjectDefaultsTo( out, o );
                        }
                        else
                        {
                            printDefaultsTo( out, descriptor.getValueToShowInHelp() );
                        }
                    }
                    catch ( Throwable t )
                    {
                        // Ignore
                    }
                }
            }
            out.println();
        }
        if ( commandDescriptor.getDetailedDescription() != null
            && commandDescriptor.getDetailedDescription().length() > 0 )
        {
            out.println(
                Ansi.ansi()
                    .a( Ansi.Attribute.INTENSITY_BOLD )
                    .a( "DETAILS" )
                    .a( Ansi.Attribute.RESET )
            );
            String desc = loadDescription( action.getClass(), commandDescriptor.getDetailedDescription() );
            printFormatted( "        ", desc, term != null ? term.getWidth() : 80, out );
        }
    }

    private void printObjectDefaultsTo( final PrintStream out, final Object o )
    {
        if ( o != null
            && ( !( o instanceof Boolean ) || ( (Boolean) o ) )
            && ( !( o instanceof Number ) || ( (Number) o ).doubleValue() != 0.0 ) )
        {
            printDefaultsTo( out, o.toString() );
        }
    }

    private void printDefaultsTo( PrintStream out, String value )
    {
        out.print( "                (defaults to " );
        out.print( value );
        out.println( ")" );
    }

    protected String loadDescription( final Class clazz, final String description )
    {
        String resolved = description;
        if ( description.startsWith( "classpath:" ) )
        {
            InputStream is = clazz.getResourceAsStream( resolved.substring( "classpath:".length() ) );
            if ( is == null )
            {
                is = clazz.getClassLoader().getResourceAsStream( resolved.substring( "classpath:".length() ) );
            }
            if ( is == null )
            {
                resolved = "Unable to load description from " + description;
            }
            else
            {
                try
                {
                    Reader r = new InputStreamReader( is );
                    StringWriter sw = new StringWriter();
                    int c;
                    while ( ( c = r.read() ) != -1 )
                    {
                        sw.append( (char) c );
                    }
                    resolved = sw.toString();
                }
                catch ( IOException e )
                {
                    resolved = "Unable to load description from " + description;
                }
                finally
                {
                    try
                    {
                        is.close();
                    }
                    catch ( IOException e )
                    {
                        // Ignore
                    }
                }
            }
        }
        return resolved;
    }

    // TODO move this to a helper class?
    public static void printFormatted( String prefix, String str, int termWidth, PrintStream out )
    {
        int pfxLen = length( prefix );
        int maxwidth = termWidth - pfxLen;
        Pattern wrap = Pattern.compile( "(\\S\\S{" + maxwidth + ",}|.{1," + maxwidth + "})(\\s+|$)" );
        Matcher m = wrap.matcher( str );
        while ( m.find() )
        {
            out.print( prefix );
            out.println( m.group() );
        }
    }

    public static int length( String str )
    {
        return str.length();
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
