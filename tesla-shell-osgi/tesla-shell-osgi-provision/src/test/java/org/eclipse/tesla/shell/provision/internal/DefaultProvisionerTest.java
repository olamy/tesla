package org.eclipse.tesla.shell.provision.internal;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import javax.inject.Inject;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.eclipse.tesla.shell.provision.Storage;
import org.eclipse.tesla.shell.provision.internal.mosgi.ExecutionEnvironment;
import org.eclipse.tesla.shell.provision.internal.mosgi.MockOsgiFramework;
import org.eclipse.tesla.shell.provision.url.Reference;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.sonatype.guice.bean.containers.InjectedTest;
import com.google.inject.Binder;

/**
 * TODO
 *
 * @since 1.0
 */
public class DefaultProvisionerTest
    extends InjectedTest
{

    static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";

    @Inject
    private DefaultProvisioner underTest;

    private BundleContext bundleContext;

    @Override
    public void setUp()
    {
        System.setProperty( JAVA_PROTOCOL_HANDLER_PKGS, Reference.class.getPackage().getName() );

        final MockOsgiFramework osgiFramework = new MockOsgiFramework();
        osgiFramework.withExecutionEnvironment(
            ExecutionEnvironment.J2SE_1_3,
            ExecutionEnvironment.J2SE_1_4,
            ExecutionEnvironment.J2SE_1_5,
            ExecutionEnvironment.JavaSE_1_6
        );
        bundleContext = osgiFramework.getBundleContext();

        super.setUp();
    }

    @Override
    public void tearDown()
    {
        System.getProperties().remove( JAVA_PROTOCOL_HANDLER_PKGS );
        super.tearDown();
    }

    @Override
    public void configure( final Binder binder )
    {
        final RepositoryAdminImpl rai = new RepositoryAdminImpl( bundleContext, new Logger( bundleContext ) );

        binder.bind( Storage.class ).to( TempDirStorage.class );
        binder.bind( RepositoryAdmin.class ).toInstance( rai );
    }

    @Test
    public void test()
        throws BundleException
    {
        final ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass( String.class );
        when(
            bundleContext.installBundle( anyString(), Matchers.<InputStream>any() )
        ).thenReturn( mock( Bundle.class ) );
        underTest.provision( "ch.qos.logback:logback-classic:0.9.30", "org.sonatype.aether:aether-impl:1.13" );
        verify( bundleContext, times( 19 ) ).installBundle( locationCaptor.capture(), Matchers.<InputStream>any() );
        for ( final String location : locationCaptor.getAllValues() )
        {
            System.out.println( location );
        }
    }

}
