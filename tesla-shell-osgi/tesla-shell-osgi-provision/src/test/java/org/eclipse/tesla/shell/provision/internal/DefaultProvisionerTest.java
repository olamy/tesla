package org.eclipse.tesla.shell.provision.internal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.ops4j.pax.sham.core.support.PackageUtils.packagesOf;

import java.io.InputStream;
import javax.inject.Inject;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.eclipse.tesla.shell.provision.url.Reference;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.ops4j.pax.sham.core.ExecutionEnvironment;
import org.ops4j.pax.sham.core.OSGiFramework;
import org.ops4j.pax.sham.core.ShamFramework;
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

        final ShamFramework osgiFramework = new ShamFramework().withExecutionEnvironment(
            ExecutionEnvironment.J2SE_1_3,
            ExecutionEnvironment.J2SE_1_4,
            ExecutionEnvironment.J2SE_1_5,
            ExecutionEnvironment.JavaSE_1_6
        );
        osgiFramework.getSystemBundle()
            .withPackages( packagesOf( ExecutionEnvironment.JavaSE_1_6 ) )
            .withPackages( packagesOf( OSGiFramework.OSGI_4_2 ) );
        bundleContext = osgiFramework.getSystemBundle().getBundleContext();

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

        binder.bind( BundleContext.class ).toInstance( bundleContext );
        binder.bind( RepositoryAdmin.class ).toInstance( rai );
    }

    @Test
    public void installAether()
        throws BundleException
    {
        final ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass( String.class );

        underTest.install(
            "ch.qos.logback:logback-classic:0.9.30",
            "org.sonatype.aether:aether-impl:1.13"
        );

        verify( bundleContext, times( 16 ) ).installBundle( locationCaptor.capture(), Matchers.<InputStream>any() );
        for ( final String location : locationCaptor.getAllValues() )
        {
            System.out.println( location );
        }
    }

    @Test
    public void installMaven()
        throws BundleException
    {
        final ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass( String.class );

        underTest.install(
            "ch.qos.logback:logback-classic:0.9.30",
            "org.apache.maven:maven-embedder:3.0.3"
        );

        verify( bundleContext, times( 31 ) ).installBundle( locationCaptor.capture(), Matchers.<InputStream>any() );
        for ( final String location : locationCaptor.getAllValues() )
        {
            System.out.println( location );
        }
    }

}
