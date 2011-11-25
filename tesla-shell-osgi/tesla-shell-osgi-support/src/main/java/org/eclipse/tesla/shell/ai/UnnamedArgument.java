package org.eclipse.tesla.shell.ai;

import java.lang.annotation.Annotation;

import org.apache.karaf.shell.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class UnnamedArgument
    implements Argument
{

    private final String name;

    private final Argument delegate;

    public UnnamedArgument( final String name, final Argument delegate )
    {
        this.name = name;
        this.delegate = delegate;
    }

    public String name()
    {
        return name;
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

}
