package org.eclipse.tesla.shell.gshell.internal.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.felix.gogo.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class MethodArgumentAdapter
    implements Argument
{

    private final org.sonatype.gshell.util.cli2.Argument delegate;

    private final Method method;

    public MethodArgumentAdapter( final org.sonatype.gshell.util.cli2.Argument delegate,
                                  final Method method )
    {
        this.delegate = delegate;
        this.method = method;
    }

    public String name()
    {
        return method.getName();
    }

    public String description()
    {
        return delegate.description();
    }

    public boolean required()
    {
        return delegate.required();
    }

    public int index()
    {
        return delegate.index();
    }

    public boolean multiValued()
    {
        return method.getParameterTypes()[0].isArray()
            || Collection.class.isAssignableFrom( method.getParameterTypes()[0] );
    }

    public String valueToShowInHelp()
    {
        return DEFAULT_STRING;
    }

    public Class<? extends Annotation> annotationType()
    {
        return Argument.class;
    }

    @Override
    public String toString()
    {
        return String.format( "%s (index=%s, required=%s, multiple-values=%s)",
                              name(), index(), required(), multiValued()
        );
    }

}
