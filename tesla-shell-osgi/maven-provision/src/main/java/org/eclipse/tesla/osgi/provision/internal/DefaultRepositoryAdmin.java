/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.internal;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.impl.RepositoryAdminImpl;
import org.apache.felix.utils.log.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
public class DefaultRepositoryAdmin
    extends RepositoryAdminImpl
{

    @Inject
    public DefaultRepositoryAdmin( final BundleContext context,
                                   final Logger logger )
    {
        super(
            (BundleContext) Proxy.newProxyInstance(
                DefaultRepositoryAdmin.class.getClassLoader(),
                new Class<?>[]{ BundleContext.class },
                new InstallLocationFixture( context )
            ),
            logger
        );
    }

    private static class InstallLocationFixture
        implements InvocationHandler
    {

        private final BundleContext delegate;

        private InstallLocationFixture( final BundleContext delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public Object invoke( final Object target, final Method method, final Object[] params )
            throws Throwable
        {
            if ( "installBundle".equals( method.getName() ) && params.length == 2 )
            {
                return installBundle( (String) params[0], (InputStream) params[1] );
            }
            return method.invoke( delegate, params );
        }

        private Object installBundle( final String location, final InputStream stream )
            throws BundleException
        {
            if ( stream instanceof URLAwareInputStream )
            {
                return delegate.installBundle( ( (URLAwareInputStream) stream ).getUrl().toExternalForm(), stream );
            }
            return delegate.installBundle( location, stream );
        }

    }

}
