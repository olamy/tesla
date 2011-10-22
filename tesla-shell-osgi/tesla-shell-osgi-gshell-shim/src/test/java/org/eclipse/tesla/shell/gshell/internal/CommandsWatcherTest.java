package org.eclipse.tesla.shell.gshell.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.support.spi.ShellCommand;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.gshell.guice.CoreModule;
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

    private ArgumentCaptor<ShellCommand> commandCaptor = ArgumentCaptor.forClass( ShellCommand.class );

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
    }

    @Test
    public void test()
        throws Exception
    {
        final ShellCommand command = getCommand( "cd" );
        command.execute( commandSession, Arrays.<Object>asList( "." ) );

    }

    private ShellCommand getCommand( final String name )
    {
        final List<ShellCommand> commands = commandCaptor.getAllValues();
        for ( final ShellCommand command : commands )
        {
            if ( name.equals( command.getName() ) )
            {
                return command;
            }
        }
        Assert.fail( "No command:" + name );
        return null;
    }

}
