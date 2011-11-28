/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * A {@link Field} {@link ActionInjector}.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class ActionFieldInjector
    implements ActionInjector
{

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private Object instance;

    private Field field;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public ActionFieldInjector( final Object instance, final Field field )
    {
        this.instance = instance;
        this.field = field;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @Override
    public Type getGenericType()
    {
        return field.getGenericType();
    }

    @Override
    public Class<?> getType()
    {
        return field.getType();
    }

    @Override
    public void set( final Object value )
        throws IllegalAccessException
    {
        field.setAccessible( true );
        field.set( instance, value );
    }

    @Override
    public Object get()
        throws IllegalAccessException
    {
        field.setAccessible( true );
        return field.get( instance );
    }

}
