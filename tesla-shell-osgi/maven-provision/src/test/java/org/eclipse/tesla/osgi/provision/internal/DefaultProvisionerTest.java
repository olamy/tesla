/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.internal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.ops4j.pax.sham.core.support.PackageUtils.packagesOf;

import java.io.InputStream;
import javax.inject.Inject;

import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.eclipse.tesla.osgi.provision.url.Reference;
import org.eclipse.tesla.osgi.provision.url.mab.internal.Connection;
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
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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

        final ShamFramework osgiFramework = new ShamFramework()
            .withExecutionEnvironment(
                ExecutionEnvironment.J2SE_1_3,
                ExecutionEnvironment.J2SE_1_4,
                ExecutionEnvironment.J2SE_1_5,
                ExecutionEnvironment.JavaSE_1_6
            )
            .withFrameworkVersion( "1.0" );
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

    public void installMaven( boolean useImportPackage, boolean useRequireBundle, int expectedNumberOfBundles )
        throws BundleException
    {
        System.setProperty( Connection.class.getName() + ".useImportPackage", String.valueOf( useImportPackage ) );
        System.setProperty( Connection.class.getName() + ".useRequireBundle", String.valueOf( useRequireBundle ) );

        final ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass( String.class );

        final ProvisionSet provisionSet = underTest.resolve(
            "ch.qos.logback:logback-classic:0.9.30",
            "org.apache.maven:maven-embedder:3.0.3"
        );
        provisionSet.install();

        verify( bundleContext, times( expectedNumberOfBundles ) ).installBundle(
            locationCaptor.capture(), Matchers.<InputStream>any()
        );
        for ( final String location : locationCaptor.getAllValues() )
        {
            System.out.println( location );
        }
    }

    @Test
    public void installMaven1()
        throws BundleException
    {
        installMaven( true, false, 31 );
    }

    @Test
    public void installMaven2()
        throws BundleException
    {
        installMaven( true, true, 33 );
    }

    @Test
    public void installMaven3()
        throws BundleException
    {
        installMaven( false, true, 35 );
    }

}
