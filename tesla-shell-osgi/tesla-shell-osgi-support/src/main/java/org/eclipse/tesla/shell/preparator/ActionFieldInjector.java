/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * A {@link Field} {@link ActionInjector}.
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
