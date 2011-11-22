package org.eclipse.tesla.shell.support.internal.adapter;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.felix.gogo.commands.Option;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionAdapter
    implements Option
{

    private org.eclipse.tesla.shell.support.Option delegate;

    public OptionAdapter( final org.eclipse.tesla.shell.support.Option delegate )
    {
        this.delegate = delegate;
    }

    public String name()
    {
        return delegate.name();
    }

    public String[] aliases()
    {
        return delegate.aliases();
    }

    public String description()
    {
        return delegate.description();
    }

    public boolean required()
    {
        return delegate.required();
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
