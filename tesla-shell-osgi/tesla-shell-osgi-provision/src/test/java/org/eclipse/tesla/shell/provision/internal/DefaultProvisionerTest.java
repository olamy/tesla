package org.eclipse.tesla.shell.provision.internal;

import javax.inject.Inject;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.eclipse.tesla.shell.provision.Storage;
import org.eclipse.tesla.shell.provision.internal.mosgi.MockOsgiFramework;
import org.eclipse.tesla.shell.provision.url.Reference;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
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

    @Override
    public void setUp()
    {
        super.setUp();
        System.setProperty( JAVA_PROTOCOL_HANDLER_PKGS, Reference.class.getPackage().getName() );
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
        final BundleContext bundleContext = new MockOsgiFramework().getBundleContext();
        final RepositoryAdminImpl rai = new RepositoryAdminImpl( bundleContext, new Logger( bundleContext ) );

        binder.bind( Storage.class ).to( TempDirStorage.class );
        binder.bind( RepositoryAdmin.class ).toInstance( rai );
    }

    @Test
    public void test()
    {
        underTest.provision( "org.sonatype.aether:aether-impl:1.13" );
    }

}
