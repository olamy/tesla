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

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.console.NameScoping;
import org.fusesource.jansi.Ansi;
import jline.Terminal;

public class UsagePrinter
{

    static final String PADDING = "        ";

    static final String PADDINGx2 = PADDING + PADDING;

    public static void print( final CommandSession session,
                              final Action action,
                              final CommandDescriptor commandDescriptor,
                              final List<OptionDescriptor> optionDescriptors,
                              final List<ArgumentDescriptor> argumentDescriptors,
                              final PrintStream out )
    {
        optionDescriptors.add(
            new OptionDescriptor()
                .setName( "-h" )
                .setAliases( "--help" )
                .setDescription( "Display this help message" )
        );

        final int termWidth = determineTerminalWidth( session );
        final boolean globalScope = NameScoping.isGlobalScope( session, commandDescriptor.getScope() );

        printCommandDescription( commandDescriptor, globalScope, out );
        printSyntax( commandDescriptor, optionDescriptors, argumentDescriptors, globalScope, out );
        printArguments( argumentDescriptors, termWidth, out );
        printOptions( optionDescriptors, termWidth, out );
        printDetailedDescription( action, commandDescriptor, termWidth, out );
    }

    private static void printCommandDescription( final CommandDescriptor descriptor,
                                                 final boolean globalScope,
                                                 final PrintStream out )
    {
        if ( descriptor != null && ( descriptor.getDescription() != null || descriptor.getName() != null ) )
        {
            out.println( bold( "DESCRIPTION" ) );
            out.print( PADDING );
            if ( descriptor.getName() != null )
            {
                if ( !globalScope )
                {
                    out.print( plain( descriptor.getScope() + ":" ) );
                }
                out.println( bold( descriptor.getName() ) );
                out.println();
            }
            out.print( "\t" );
            out.println( descriptor.getDescription() );
            out.println();
        }
    }

    private static void printSyntax( final CommandDescriptor commandDescriptor,
                                     final List<OptionDescriptor> optionDescriptors,
                                     final List<ArgumentDescriptor> argumentDescriptors,
                                     final boolean globalScope,
                                     final PrintStream out )
    {
        final StringBuilder syntax = new StringBuilder();
        if ( commandDescriptor != null )
        {
            if ( globalScope )
            {
                syntax.append( commandDescriptor.getName() );
            }
            else
            {
                syntax.append( format( "%s:%s", commandDescriptor.getScope(), commandDescriptor.getName() ) );
            }
        }
        if ( optionDescriptors.size() > 0 )
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
                    syntax.append( format( "[%s] ", descriptor.getName() ) );
                }
                else
                {
                    syntax.append( format( "%s ", descriptor.getName() ) );
                }
            }
        }
        out.println( bold( "SYNTAX" ) );
        out.print( PADDING );
        out.println( syntax.toString() );
        out.println();
    }

    private static void printArguments( final List<ArgumentDescriptor> descriptors,
                                        final int terminalWidth,
                                        final PrintStream out )
    {
        if ( descriptors.size() > 0 )
        {
            out.println( bold( "ARGUMENTS" ) );
            for ( final ArgumentDescriptor descriptor : descriptors )
            {
                out.print( PADDING );
                out.println( bold( descriptor.getName() ) );
                printFormatted( PADDINGx2, descriptor.getDescription(), terminalWidth, out );

                final String valueToShowInHelp = descriptor.getValueToShowInHelp();
                if ( valueToShowInHelp != null && valueToShowInHelp.length() != 0 )
                {
                    try
                    {
                        if ( ArgumentDescriptor.DEFAULT.equals( valueToShowInHelp ) )
                        {
                            printObjectDefaults( descriptor.getInjector().get(), out );
                        }
                        else
                        {
                            printDefaults( valueToShowInHelp, out );
                        }
                    }
                    catch ( Throwable ignore )
                    {
                        // Ignore
                    }
                }
            }
            out.println();
        }
    }

    private static void printOptions( final List<OptionDescriptor> descriptors,
                                      final int terminalWidth,
                                      final PrintStream out )
    {
        if ( descriptors.size() > 0 )
        {
            out.println( bold( "OPTIONS" ) );
            for ( final OptionDescriptor descriptor : descriptors )
            {
                String names = descriptor.getName();
                for ( final String alias : descriptor.getAliases() )
                {
                    names += ", " + alias;
                }
                out.print( PADDING );
                out.println( bold( names ) );
                printFormatted( PADDINGx2, descriptor.getDescription(), terminalWidth, out );

                final String valueToShowInHelp = descriptor.getValueToShowInHelp();
                if ( valueToShowInHelp != null && valueToShowInHelp.length() != 0 )
                {
                    try
                    {
                        if ( OptionDescriptor.DEFAULT.equals( valueToShowInHelp ) )
                        {
                            printObjectDefaults( descriptor.getInjector().get(), out );
                        }
                        else
                        {
                            printDefaults( valueToShowInHelp, out );
                        }
                    }
                    catch ( Throwable ignore )
                    {
                        // Ignore
                    }
                }
            }
            out.println();
        }
    }

    private static void printDetailedDescription( final Action action,
                                                  final CommandDescriptor descriptor,
                                                  final int terminalWidth,
                                                  final PrintStream out )
    {
        if ( descriptor.getDetailedDescription() != null
            && descriptor.getDetailedDescription().length() > 0 )
        {
            out.println( bold( "DETAILS" ) );
            printFormatted(
                PADDING,
                loadDescription( action.getClass(), descriptor.getDetailedDescription() ),
                terminalWidth,
                out
            );
        }
    }

    private static void printObjectDefaults( final Object object, final PrintStream out )
    {
        if ( object != null
            && ( !( object instanceof Boolean ) || ( (Boolean) object ) )
            && ( !( object instanceof Number ) || ( (Number) object ).doubleValue() != 0.0 ) )
        {
            printDefaults( object.toString(), out );
        }
    }

    private static void printDefaults( final String value, final PrintStream out )
    {
        out.print( PADDINGx2 + "(defaults to " );
        out.print( value );
        out.println( ")" );
    }

    protected static String loadDescription( final Class clazz, final String description )
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

    public static void printFormatted( final String prefix,
                                       final String str,
                                       final int termWidth,
                                       final PrintStream out )
    {
        final int pfxLen = prefix.length();
        final int maxWidth = termWidth - pfxLen;
        final Pattern wrap = Pattern.compile( "(\\S\\S{" + maxWidth + ",}|.{1," + maxWidth + "})(\\s+|$)" );
        final Matcher m = wrap.matcher( str );
        while ( m.find() )
        {
            out.print( prefix );
            out.println( m.group() );
        }
    }

    private static String bold( final String value )
    {
        return Ansi.ansi()
            .a( Ansi.Attribute.INTENSITY_BOLD )
            .a( value )
            .a( Ansi.Attribute.RESET )
            .toString();
    }

    private static String plain( final String value )
    {
        return Ansi.ansi()
            .a( value )
            .toString();
    }

    private static int determineTerminalWidth( final CommandSession session )
    {
        final Terminal term = session != null ? (Terminal) session.get( ".jline.terminal" ) : null;
        return term != null ? term.getWidth() : 80;
    }

}
