package org.eclipse.tesla.shell.ai;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * TODO
 *
 * @since 1.0
 */
public class ActionFieldInjector
    implements ActionInjector
{

    private Object instance;

    private Field field;

    public ActionFieldInjector( final Object instance, final Field field )
    {
        this.instance = instance;
        this.field = field;
    }

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
