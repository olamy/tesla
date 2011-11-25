package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * TODO
 *
 * @since 1.0
 */
public class ActionMethodInjector
    implements ActionInjector
{

    private Object instance;

    private final Method method;

    public ActionMethodInjector( final Object instance, final Method method )
    {
        this.instance = instance;
        this.method = method;
    }

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
