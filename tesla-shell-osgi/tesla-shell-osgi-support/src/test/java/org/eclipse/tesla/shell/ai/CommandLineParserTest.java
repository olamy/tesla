package org.eclipse.tesla.shell.ai;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void testAction()
        throws Exception
    {
        final CommandLineParser underTest = new CommandLineParser();
        final Command1 command1 = new Command1();
        underTest.prepare( command1, session, $( "-opt1", "false", "-opt2", "arg1-value" ) );
        assertThat( command1.opt1, is( false ) );
        assertThat( command1.opt2, is( true ) );
        assertThat( command1.arg1, is( equalTo( "arg1-value" ) ) );
    }

    private List<Object> $( final Object... params )
    {
        return Arrays.asList( params );
    }

    @Command( scope = "test-scope", name = "command-1" )
    private static class Command1
        extends AbstractTestAction
    {

        @Option( name = "-opt1" )
        private boolean opt1 = true;

        @Option( name = "-opt2", required = true )
        private boolean opt2;

        @Argument()
        private String arg1;

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
