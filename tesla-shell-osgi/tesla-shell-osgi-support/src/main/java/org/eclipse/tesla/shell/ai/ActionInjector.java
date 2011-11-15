package org.eclipse.tesla.shell.ai;

import java.lang.reflect.Type;

/**
 * TODO
 *
 * @since 1.0
 */
public interface ActionInjector
{

    Type getGenericType();

    Class<?> getType();

    Object get()
        throws IllegalAccessException;

    void set( Object value )
        throws IllegalAccessException;

}
