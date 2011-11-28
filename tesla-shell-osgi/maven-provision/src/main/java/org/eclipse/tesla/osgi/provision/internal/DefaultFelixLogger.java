/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.utils.log.Logger;
import org.osgi.framework.BundleContext;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
public class DefaultFelixLogger
    extends Logger
{

    @Inject
    public DefaultFelixLogger( final BundleContext context )
    {
        super( context );
    }

}
