package org.eclipse.tesla.shell.preparator;

import java.lang.reflect.Type;

/**
 * TODO
 *
 * @since 1.0
 */
public class ActionVoidInjector
    implements ActionInjector
{

    public static final ActionVoidInjector INSTANCE = new ActionVoidInjector();

    private ActionVoidInjector()
    {
        // we have a singleton instance
    }

    @Override
    public Type getGenericType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set( final Object value )
        throws IllegalAccessException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get()
        throws IllegalAccessException
    {
        throw new UnsupportedOperationException();
    }

}
