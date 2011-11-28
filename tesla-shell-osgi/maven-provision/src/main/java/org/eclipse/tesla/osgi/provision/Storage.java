/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.tesla.osgi.provision.internal.TempDirStorage;
import com.google.inject.ImplementedBy;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@ImplementedBy( TempDirStorage.class )
public interface Storage
{

    InputStream inputStreamFor( String path );

    OutputStream outputStreamFor( String path );

    boolean exists( String path );

}
