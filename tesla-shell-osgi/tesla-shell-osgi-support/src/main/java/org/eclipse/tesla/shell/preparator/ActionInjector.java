/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Type;

/**
 * An argument/option injector used to inject the argument/option value and provide information about injection point.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public interface ActionInjector
{

    Type getGenericType();

    Class<?> getType();

    Object get()
        throws IllegalAccessException;

    void set( Object value )
        throws Exception;

}
