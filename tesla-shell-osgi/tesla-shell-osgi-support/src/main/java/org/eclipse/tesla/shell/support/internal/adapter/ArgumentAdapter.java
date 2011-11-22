package org.eclipse.tesla.shell.support.internal.adapter;

import java.lang.annotation.Annotation;

import org.apache.felix.gogo.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentAdapter
    implements Argument
{

    private final org.eclipse.tesla.shell.support.Argument delegate;

    public ArgumentAdapter( final org.eclipse.tesla.shell.support.Argument delegate )
    {
        this.delegate = delegate;
    }

    public String name()
    {
        return delegate.name();
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
        return delegate.multiValued();
    }

    public String valueToShowInHelp()
    {
        return delegate.valueToShowInHelp();
    }

    public Class<? extends Annotation> annotationType()
    {
        return delegate.annotationType();
    }

    @Override
    public String toString()
    {
        return delegate.toString();
    }

}
