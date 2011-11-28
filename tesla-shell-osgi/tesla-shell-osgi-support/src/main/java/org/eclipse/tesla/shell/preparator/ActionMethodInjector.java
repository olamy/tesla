/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A {@link Method} {@link ActionInjector}.
 */
public class ActionMethodInjector
    implements ActionInjector
{

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private Object instance;

    private final Method method;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ActionMethodInjector( final Object instance, final Method method )
    {
        this.instance = instance;
        this.method = method;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @Override
    public Type getGenericType()
    {
        return method.getGenericParameterTypes()[0];
    }

    @Override
    public Class<?> getType()
    {
        return method.getParameterTypes()[0];
    }

    @Override
    public void set( final Object value )
        throws Exception
    {
        method.setAccessible( true );
        try
        {
            method.invoke( instance, value );
        }
        catch ( InvocationTargetException e )
        {
            throw new Exception( e.getMessage(), e.getTargetException() );
        }
    }

    @Override
    public Object get()
        throws IllegalAccessException
    {
        return null;
    }

}
