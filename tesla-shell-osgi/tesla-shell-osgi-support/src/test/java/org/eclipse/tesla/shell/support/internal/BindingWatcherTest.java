package org.eclipse.tesla.shell.support.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.support.GuiceOsgiCommandSupport;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturingMatcher;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.guice.bean.containers.InjectedTest;
import com.google.inject.Binder;

/**
 * TODO
 *
 * @since 1.0
 */
public class BindingWatcherTest
    extends InjectedTest
{

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration serviceRegistration;

    @Mock
    private CommandSession commandSession;

    private CapturingMatcher<ActionProxy> testActionCaptor = new CapturingMatcher<ActionProxy>();

    private CapturingMatcher<Dictionary> testActionPropertiesCaptor = new CapturingMatcher<Dictionary>();

    private CapturingMatcher<TestCommand> testCommandCaptor = new CapturingMatcher<TestCommand>();

    private CapturingMatcher<Dictionary> testCommandPropertiesCaptor = new CapturingMatcher<Dictionary>();

    private CapturingMatcher<TestFunction> testFunctionCaptor = new CapturingMatcher<TestFunction>();

    private CapturingMatcher<Dictionary> testFunctionPropertiesCaptor = new CapturingMatcher<Dictionary>();

    @Override
    public void configure( final Binder binder )
    {
        binder.bind( BundleContext.class ).toInstance( bundleContext );
    }

    @Override
    public void setUp()
    {
        initMocks( this );
        when(
            bundleContext.registerService(
                eq( new String[]{ Function.class.getName(), CompletableFunction.class.getName() } ),
                capture( ActionProxy.class, testActionCaptor ),
                capture( Dictionary.class, testActionPropertiesCaptor )
            )
        ).thenReturn( serviceRegistration );

        when(
            bundleContext.registerService(
                eq( new String[]{ Function.class.getName(), CompletableFunction.class.getName() } ),
                capture( TestCommand.class, testCommandCaptor ),
                capture( Dictionary.class, testCommandPropertiesCaptor )
            )
        ).thenReturn( serviceRegistration );

        when(
            bundleContext.registerService(
                eq( new String[]{ Function.class.getName(), CompletableFunction.class.getName() } ),
                capture( TestFunction.class, testFunctionCaptor ),
                capture( Dictionary.class, testFunctionPropertiesCaptor )
            )
        ).thenReturn( serviceRegistration );

        super.setUp();
    }

    @Override
    public void tearDown()
    {
        super.tearDown();
        verify( serviceRegistration, times( 3 ) ).unregister();
    }

    @Test
    public void testAction()
        throws Exception
    {
        testActionCaptor.getLastValue().execute( commandSession, Arrays.<Object>asList( "argument" ) );
        assertThat( TestAction.executed, is( notNullValue() ) );
        assertThat( TestAction.executed.arg, is( equalTo( "argument" ) ) );

        final Dictionary dictionary = testActionPropertiesCaptor.getLastValue();
        assertThat( dictionary, is( notNullValue() ) );

        final String scope = (String) dictionary.get( "osgi.command.scope" );
        assertThat( scope, is( equalTo( "test-scope" ) ) );

        final String function = (String) dictionary.get( "osgi.command.function" );
        assertThat( function, is( equalTo( "test-action" ) ) );

        final String implementation = (String) dictionary.get( "implementationClass" );
        assertThat( implementation, is( equalTo( TestAction.class.getName() ) ) );
    }

    @Test
    public void testCommand()
        throws Exception
    {
        testCommandCaptor.getLastValue().execute( commandSession, Arrays.<Object>asList( "argument" ) );
        assertThat( testCommandCaptor.getLastValue().action.executed, is( notNullValue() ) );
        assertThat( testCommandCaptor.getLastValue().action.executed.arg, is( equalTo( "argument" ) ) );

        final Dictionary dictionary = testCommandPropertiesCaptor.getLastValue();
        assertThat( dictionary, is( notNullValue() ) );

        final String scope = (String) dictionary.get( "osgi.command.scope" );
        assertThat( scope, is( equalTo( "test-scope" ) ) );

        final String function = (String) dictionary.get( "osgi.command.function" );
        assertThat( function, is( equalTo( "test-command" ) ) );

        final String implementation = (String) dictionary.get( "implementationClass" );
        assertThat( implementation, is( equalTo( TestCommand.class.getName() ) ) );
    }

    @Test
    public void testFunction()
        throws Exception
    {
        testFunctionCaptor.getLastValue().execute( commandSession, Arrays.<Object>asList( "argument" ) );
        assertThat( TestFunction.executed, is( notNullValue() ) );
        assertThat( TestFunction.executed.arg, is( equalTo( "argument" ) ) );

        final Dictionary dictionary = testFunctionPropertiesCaptor.getLastValue();
        assertThat( dictionary, is( notNullValue() ) );

        final String scope = (String) dictionary.get( "osgi.command.scope" );
        assertThat( scope, is( equalTo( "test-scope" ) ) );

        final String function = (String) dictionary.get( "osgi.command.function" );
        assertThat( function, is( equalTo( "test-function" ) ) );

        final String implementation = (String) dictionary.get( "implementationClass" );
        assertThat( implementation, is( equalTo( TestFunction.class.getName() ) ) );
    }

    private <T> T capture( final Class<T> clazz, final CapturingMatcher<T> capturingMatcher )
    {
        return argThat( new ArgumentMatcher<T>()
        {
            @Override
            public boolean matches( final Object argument )
            {
                if(isA( clazz ).matches( argument ))
                {
                    capturingMatcher.captureFrom( argument );
                    return true;
                }
                return false;
            }
        } );
    }

    @Named
    @Command( scope = "test-scope", name = "test-action", description = "Test" )
    private static class TestAction
        extends GuiceOsgiCommandSupport
    {

        @Argument( name = "arg", required = true )
        private String arg;

        @Option( name = "-o", aliases = { "--option" } )
        private String option;

        @Option( name = "-b", aliases = { } )
        private String boolOption;

        private static TestAction executed;

        @Override
        protected Object doExecute()
            throws Exception
        {
            executed = this;
            return null;
        }

    }

    @Named
    @Command( scope = "test-scope", name = "test-command", description = "Provision jars" )
    private static class TestCommand
        extends AbstractCommand
    {

        private TestAction action = new TestAction();

        @Override
        public Action createNewAction()
        {
            return action;
        }

    }

    @Named
    @Command( scope = "test-scope", name = "test-function", description = "Provision jars" )
    private static class TestFunction
        implements Function
    {

        private static TestFunction executed;

        private String arg;

        public Object execute( final CommandSession commandSession, final List<Object> objects )
            throws Exception
        {
            executed = this;
            arg = (String) objects.get( 0 );
            return null;
        }

    }

}
