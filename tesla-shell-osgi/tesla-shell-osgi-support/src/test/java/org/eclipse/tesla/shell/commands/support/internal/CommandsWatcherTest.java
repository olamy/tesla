package org.eclipse.tesla.shell.commands.support.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Dictionary;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.commands.support.GuiceOsgiCommandSupport;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.guice.bean.containers.InjectedTest;
import com.google.inject.Binder;

/**
 * TODO
 *
 * @since 1.0
 */
public class CommandsWatcherTest
    extends InjectedTest
{

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration serviceRegistration;

    @Mock
    private CommandSession commandSession;

    private ArgumentCaptor<GuiceShellCommand> commandCaptor = ArgumentCaptor.forClass( GuiceShellCommand.class );

    private ArgumentCaptor<Dictionary> servicePropertiesCaptor = ArgumentCaptor.forClass( Dictionary.class );

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
                commandCaptor.capture(),
                servicePropertiesCaptor.capture()
            )
        ).thenReturn( serviceRegistration );

        super.setUp();
    }

    @Override
    public void tearDown()
    {
        super.tearDown();
        verify( serviceRegistration ).unregister();

        final Dictionary dictionary = servicePropertiesCaptor.getValue();
        assertThat( dictionary, is( notNullValue() ) );

        final String scope = (String) dictionary.get( "osgi.command.scope" );
        assertThat( scope, is( equalTo( "test-scope" ) ) );

        final String function = (String) dictionary.get( "osgi.command.function" );
        assertThat( function, is( equalTo( "test-command" ) ) );

        final String implementation = (String) dictionary.get( "implementationClass" );
        assertThat( implementation, is( equalTo( TestCommand.class.getName() ) ) );
    }

    @Test
    public void test()
        throws Exception
    {
        commandCaptor.getValue().execute( commandSession,
                                          Arrays.<Object>asList( "argument" ) );
        assertThat( TestCommand.executed, is( notNullValue() ) );
        assertThat( TestCommand.executed.arg, is( equalTo( "argument" ) ) );
    }

    @Named
    @Command( scope = "test-scope", name = "test-command", description = "Provision jars" )
    public static class TestCommand
        extends GuiceOsgiCommandSupport
    {

        @Argument( name = "arg", required = true )
        private String arg;

        @Option( name = "-o", aliases = { "--option" } )
        private String option;

        @Option( name = "-b", aliases = {} )
        private String boolOption;


        private static TestCommand executed;

        @Override
        protected Object doExecute()
            throws Exception
        {
            executed = this;
            return null;
        }

    }

}
