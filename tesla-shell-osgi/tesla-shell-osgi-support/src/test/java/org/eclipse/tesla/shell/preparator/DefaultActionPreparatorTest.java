/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredArgumentException;
import org.eclipse.tesla.shell.preparator.validation.MissingRequiredOptionException;
import org.eclipse.tesla.shell.preparator.validation.MultipleArgumentsWithSameIndexException;
import org.eclipse.tesla.shell.preparator.validation.MultipleOptionsWithSameNameException;
import org.eclipse.tesla.shell.preparator.validation.TooManyArgumentsException;
import org.eclipse.tesla.shell.preparator.validation.TooManyOptionsException;
import org.junit.Test;

/**
 * {@link DefaultActionPreparator} UTs.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class DefaultActionPreparatorTest
{

    final CommandSession session = mock( CommandSession.class );

    // ----------------------------------------------------------------------
    // no option, no arguments
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-01" )
    private static class Command01
        extends AbstractTestAction
    {

    }

    @Test
    public void parse01()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command01 command = new Command01();
        underTest.prepare( command, session, $() );
    }

    // ----------------------------------------------------------------------
    // one option, no arguments
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-02" )
    private static class Command02
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

    }

    @Test
    public void parse02()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command02 command = new Command02();
        underTest.prepare( command, session, $( "-opt1", "t-o-1" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
    }

    // ----------------------------------------------------------------------
    // more options, no arguments
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-03" )
    private static class Command03
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2" )
        private boolean opt2;

        @Option( name = "-opt3" )
        private int opt3;

        @Option( name = "-opt4" )
        private File opt4;

    }

    @Test
    public void parse03()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command03 command = new Command03();
        underTest.prepare( command, session, $( "-opt1", "t-o-1", "-opt2", "-opt3", "3", "-opt4", "t-o-4" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
        assertThat( command.opt2, is( true ) );
        assertThat( command.opt3, is( 3 ) );
        assertThat( command.opt4, is( new File( "t-o-4" ) ) );
    }

    // ----------------------------------------------------------------------
    // no options, one argument
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-04" )
    private static class Command04
        extends AbstractTestAction
    {

        @Argument()
        private String arg1;

    }

    @Test
    public void parse04()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command04 command = new Command04();
        underTest.prepare( command, session, $( "t-a-1" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
    }

    // ----------------------------------------------------------------------
    // no options, more arguments
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-05" )
    private static class Command05
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1 )
        private boolean arg2;

        @Argument( index = 2 )
        private int arg3;

        @Argument( index = 4 )
        private File arg4;

    }

    @Test
    public void parse05()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command05 command = new Command05();
        underTest.prepare( command, session, $( "t-a-1", "true", "3", "t-a-4" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( true ) );
        assertThat( command.arg3, is( 3 ) );
        assertThat( command.arg4, is( new File( "t-a-4" ) ) );
    }

    // ----------------------------------------------------------------------
    // more options, more arguments
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-06" )
    private static class Command06
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2" )
        private boolean opt2;

        @Option( name = "-opt3" )
        private int opt3;

        @Option( name = "-opt4" )
        private File opt4;

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1 )
        private boolean arg2;

        @Argument( index = 2 )
        private int arg3;

        @Argument( index = 4 )
        private File arg4;

    }

    @Test
    public void parse06()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command06 command = new Command06();
        underTest.prepare( command, session, $( "-opt1", "t-0-1", "-opt2", "-opt3", "3", "-opt4", "t-o-4",
                                                "t-a-1", "true", "3", "t-a-4" ) );
        assertThat( command.opt1, is( "t-0-1" ) );
        assertThat( command.opt2, is( true ) );
        assertThat( command.opt3, is( 3 ) );
        assertThat( command.opt4, is( new File( "t-o-4" ) ) );

        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( true ) );
        assertThat( command.arg3, is( 3 ) );
        assertThat( command.arg4, is( new File( "t-a-4" ) ) );
    }

    // ----------------------------------------------------------------------
    // one option but no provided value
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-07" )
    private static class Command07
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1 = "default";

    }

    @Test
    public void parse07()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command07 command = new Command07();
        underTest.prepare( command, session, $() );
        assertThat( command.opt1, is( "default" ) );
    }

    // ----------------------------------------------------------------------
    // no options, one multi value argument
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-08" )
    private static class Command08
        extends AbstractTestAction
    {

        @Argument( multiValued = true )
        private String[] arg1;

    }

    @Test
    public void parse08()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command08 command = new Command08();
        underTest.prepare( command, session, $( "t-a-1", "t-a-2" ) );
        assertThat( command.arg1, arrayContaining( "t-a-1", "t-a-2" ) );
    }

    // ----------------------------------------------------------------------
    // no options, two arguments, one multi valued
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-09" )
    private static class Command09
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1, multiValued = true )
        private File[] arg2;

    }

    @Test
    public void parse09()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command09 command = new Command09();
        underTest.prepare( command, session, $( "t-a-1", "t-a-2", "t-a-3" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, arrayContaining( new File( "t-a-2" ), new File( "t-a-3" ) ) );
    }

    // ----------------------------------------------------------------------
    // a switch will not consume the argument
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-10" )
    private static class Command10
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private boolean opt1;

        @Argument()
        private String arg1;

    }

    @Test
    public void parse10()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command10 command = new Command10();
        underTest.prepare( command, session, $( "-opt1", "t-a-1" ) );
        assertThat( command.opt1, is( true ) );
        assertThat( command.arg1, is( "t-a-1" ) );
    }

    // ----------------------------------------------------------------------
    // a switch will consume the argument if the arguments is true/false
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-11" )
    private static class Command11
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private boolean opt1 = true;

        @Argument()
        private String arg1;

    }

    @Test
    public void parse11()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        {
            final Command11 command = new Command11();
            underTest.prepare( command, session, $( "-opt1", "false", "t-a-1" ) );
            assertThat( command.opt1, is( false ) );
            assertThat( command.arg1, is( "t-a-1" ) );
        }
        {
            final Command11 command = new Command11();
            command.opt1 = false;
            underTest.prepare( command, session, $( "-opt1", "true", "t-a-1" ) );
            assertThat( command.opt1, is( true ) );
            assertThat( command.arg1, is( "t-a-1" ) );
        }
    }

    // ----------------------------------------------------------------------
    // two many arguments will produce an exception
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-12" )
    private static class Command12
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1 )
        private boolean arg2;

    }

    @Test( expected = TooManyArgumentsException.class )
    public void parse12()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command12 command = new Command12();
        underTest.prepare( command, session, $( "t-a-1", "true", "3", "t-a-4" ) );
    }

    // ----------------------------------------------------------------------
    // insufficient arguments will not produce exception if arguments are optional
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-13" )
    private static class Command13
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1 )
        private String arg2;

    }

    @Test
    public void parse13()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command13 command = new Command13();
        underTest.prepare( command, session, $( "t-a-1" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( nullValue() ) );
    }

    // ----------------------------------------------------------------------
    // insufficient arguments will produce exception if arguments are mandatory
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-14" )
    private static class Command14
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1, required = true )
        private String arg2;

    }

    @Test( expected = MissingRequiredArgumentException.class )
    public void parse14()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command14 command = new Command14();
        underTest.prepare( command, session, $( "t-a-1" ) );
    }

    // ----------------------------------------------------------------------
    // multiple arguments with same index will produce an exception
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-15" )
    private static class Command15
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1 )
        private String arg2;

        @Argument( index = 1 )
        private String arg3;

    }

    @Test( expected = MultipleArgumentsWithSameIndexException.class )
    public void parse15()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command15 command = new Command15();
        underTest.prepare( command, session, $( "t-a-1", "t-a-2", "t-a-3" ) );
    }

    // ----------------------------------------------------------------------
    // two many options will produce an exception
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-16" )
    private static class Command16
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

    }

    @Test( expected = TooManyOptionsException.class )
    public void parse16()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command16 command = new Command16();
        underTest.prepare( command, session, $( "-opt1", "t-o-1", "-opt2", "-opt3", "t-o-3" ) );
    }

    // ----------------------------------------------------------------------
    // insufficient options will produce an exception if options are mandatory
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-17" )
    private static class Command17
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2", required = true )
        private String opt2;

    }

    @Test( expected = MissingRequiredOptionException.class )
    public void parse17()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command17 command = new Command17();
        underTest.prepare( command, session, $( "-opt1", "t-o-1" ) );
    }

    // ----------------------------------------------------------------------
    // multiple options with same name will produce an exception
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-18" )
    private static class Command18
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt1" )
        private String opt2;

    }

    @Test( expected = MultipleOptionsWithSameNameException.class )
    public void parse18()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command18 command = new Command18();
        underTest.prepare( command, session, $( "-opt1", "t-o-1" ) );
    }

    // ----------------------------------------------------------------------
    // multiple options with same name will produce an exception
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-19" )
    private static class Command19
        extends AbstractTestAction
    {

        @Option( name = "-opt1", aliases = { "-opt2" } )
        private String opt1;

        @Option( name = "-opt2" )
        private String opt2;

    }

    @Test( expected = MultipleOptionsWithSameNameException.class )
    public void parse19()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command19 command = new Command19();
        underTest.prepare( command, session, $( "-opt1", "t-o-1" ) );
    }

    // ----------------------------------------------------------------------
    // print usage (--help)
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-20" )
    private static class Command20
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2", required = true )
        private boolean opt2;

        @Argument( index = 0, required = true )
        private String arg1;

        @Argument( index = 1 )
        private boolean arg2;

    }

    @Test
    public void parse20()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command20 command = new Command20();
        underTest.prepare( command, session, $( "--help" ) );
    }

    // ----------------------------------------------------------------------
    // mixed options/arguments order, last option without value
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-21" )
    private static class Command21
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2" )
        private String opt2;

        @Argument( index = 1 )
        private String arg1;

        @Argument( index = 2 )
        private String arg2;

    }

    @Test
    public void parse21()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command21 command = new Command21();
        underTest.prepare( command, session, $( "t-a-1", "-opt1", "t-o-1", "t-a-2", "-opt2" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
        assertThat( command.opt2, is( nullValue() ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( "t-a-2" ) );
    }

    // ----------------------------------------------------------------------
    // options on setter methods
    // ----------------------------------------------------------------------

//    @Command( scope = "test-scope", name = "command-22" )
//    private static class Command22
//        extends AbstractTestAction
//    {
//
//        @Option( name = "-opt1" )
//        private String opt1;
//
//        private String opt2;
//
//        @Option( name = "-opt2" )
//        private void opt2( String value )
//        {
//            opt2 = value;
//        }
//
//        @Argument()
//        private String arg1;
//
//    }
//
//    @Test
//    public void parse22()
//        throws Exception
//    {
//        final CommandLineParser underTest = new CommandLineParser();
//        final Command22 command = new Command22();
//        underTest.prepare( command, session, $( "-opt1", "t-o-1", "t-a-1", "-opt2", "t-o-2" ) );
//        assertThat( command.opt1, is( "t-o-1" ) );
//        assertThat( command.opt2, is( "t-o-2" ) );
//        assertThat( command.arg1, is( "t-a-1" ) );
//    }

    // ----------------------------------------------------------------------
    // mixed options/arguments order, last option is a switch
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-23" )
    private static class Command23
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2" )
        private boolean opt2;

        @Argument( index = 1 )
        private String arg1;

        @Argument( index = 2 )
        private String arg2;

    }

    @Test
    public void parse23()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command23 command = new Command23();
        underTest.prepare( command, session, $( "t-a-1", "-opt1", "t-o-1", "t-a-2", "-opt2" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
        assertThat( command.opt2, is( true ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( "t-a-2" ) );
    }

    // ----------------------------------------------------------------------
    // option is multi valued
    // ----------------------------------------------------------------------

    @Command( scope = "test-scope", name = "command-24" )
    private static class Command24
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

        @Option( name = "-opt2", multiValued = true )
        private String[] opt2;

        @Option( name = "-opt3", multiValued = true )
        private File[] opt3;

    }

    @Test
    public void parse24()
        throws Exception
    {
        final DefaultActionPreparator underTest = new DefaultActionPreparator();
        final Command24 command = new Command24();
        underTest.prepare( command, session, $( "-opt1", "t-o-1", "-opt2", "t-o-2",
                                                "-opt3", "t-o-3-1", "-opt3", "t-o-3-2" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
        assertThat( command.opt2, arrayContaining( "t-o-2" ) );
        assertThat( command.opt3, arrayContaining( new File( "t-o-3-1" ), new File( "t-o-3-2" ) ) );
    }

    private List<Object> $( final Object... params )
    {
        return Arrays.asList( params );
    }

    private static class AbstractTestAction
        implements Action
    {

        @Override
        public Object execute( final CommandSession commandSession )
            throws Exception
        {
            return null;
        }

    }

}
