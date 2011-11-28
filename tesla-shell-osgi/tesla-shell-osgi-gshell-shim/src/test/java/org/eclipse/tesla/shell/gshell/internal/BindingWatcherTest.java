/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.apache.karaf.shell.console.CompletableFunction;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.guice.bean.containers.InjectedTest;
import com.google.inject.Binder;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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

    private Map<String, AbstractCommand> commands = new HashMap<String, AbstractCommand>();

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
                any(),
                Matchers.<Dictionary>any()
            )
        ).thenAnswer( new Answer<ServiceRegistration>()
        {
            public ServiceRegistration answer( final InvocationOnMock invocation )
                throws Throwable
            {
                final Object properties = invocation.getArguments()[2];
                final String name = (String) ( (Dictionary) properties ).get( "osgi.command.function" );
                commands.put( name, (AbstractCommand) invocation.getArguments()[1] );
                return serviceRegistration;
            }
        } );

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
        final AbstractCommand command = commands.get( "cd" );
        command.execute( commandSession, Arrays.<Object>asList( "." ) );

    }

}
