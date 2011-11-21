package org.eclipse.tesla.shell.ai;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * TODO
 *
 * @since 1.0
 */
public class CommandLineParserTest
{

    final CommandSession session = Mockito.mock( CommandSession.class );

    @Command( scope = "test-scope", name = "command-01" )
    private static class Command01
        extends AbstractTestAction
    {

    }

    // no option, no arguments
    @Test
    public void parse01()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command01 command = new Command01();
        underTest.prepare( command, session, $() );
    }

    @Command( scope = "test-scope", name = "command-02" )
    private static class Command02
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1;

    }

    // one option, no arguments
    @Test
    public void parse02()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command02 command = new Command02();
        underTest.prepare( command, session, $( "-opt1", "t-o-1" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
    }

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

    // more options, no arguments
    @Test
    public void parse03()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command03 command = new Command03();
        underTest.prepare( command, session, $( "-opt1", "t-o-1", "-opt2", "-opt3", "3", "-opt4", "t-o-4" ) );
        assertThat( command.opt1, is( "t-o-1" ) );
        assertThat( command.opt2, is( true ) );
        assertThat( command.opt3, is( 3 ) );
        assertThat( command.opt4, is( new File( "t-o-4" ) ) );
    }

    @Command( scope = "test-scope", name = "command-04" )
    private static class Command04
        extends AbstractTestAction
    {

        @Argument()
        private String arg1;

    }

    // no options, one argument
    @Test
    public void parse04()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command04 command = new Command04();
        underTest.prepare( command, session, $( "t-a-1" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
    }

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

    // no options, more arguments
    @Test
    public void parse05()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command05 command = new Command05();
        underTest.prepare( command, session, $( "t-a-1", "true", "3", "t-a-4" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, is( true ) );
        assertThat( command.arg3, is( 3 ) );
        assertThat( command.arg4, is( new File( "t-a-4" ) ) );
    }

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

    // more options, more arguments
    @Test
    public void parse06()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
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

    @Command( scope = "test-scope", name = "command-07" )
    private static class Command07
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private String opt1 = "default";

    }

    // one option but no provided value
    @Test
    public void parse07()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command07 command = new Command07();
        underTest.prepare( command, session, $() );
        assertThat( command.opt1, is( "default" ) );
    }

    @Command( scope = "test-scope", name = "command-08" )
    private static class Command08
        extends AbstractTestAction
    {

        @Argument( multiValued = true )
        private String[] arg1;

    }

    // no options, one multi value argument
    @Test
    public void parse08()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command08 command = new Command08();
        underTest.prepare( command, session, $( "t-a-1", "t-a-2" ) );
        assertThat( command.arg1, arrayContaining( "t-a-1", "t-a-2" ) );
    }

    @Command( scope = "test-scope", name = "command-09" )
    private static class Command09
        extends AbstractTestAction
    {

        @Argument( index = 0 )
        private String arg1;

        @Argument( index = 1, multiValued = true )
        private File[] arg2;

    }

    // no options, two arguments, one multi valued
    @Test
    public void parse09()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command09 command = new Command09();
        underTest.prepare( command, session, $( "t-a-1", "t-a-2", "t-a-3" ) );
        assertThat( command.arg1, is( "t-a-1" ) );
        assertThat( command.arg2, arrayContaining( new File( "t-a-2" ), new File( "t-a-3" ) ) );
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
