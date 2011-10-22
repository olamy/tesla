package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.felix.gogo.commands.Argument;

/**
* TODO
*
* @since 1.0
*/
class GShellShimArgument
implements Argument
{

    private final org.sonatype.gshell.util.cli2.Argument delegate;

    private final Field field;

    GShellShimArgument( final org.sonatype.gshell.util.cli2.Argument delegate,
                        final Field field )
    {
        this.delegate = delegate;
        this.field = field;
    }

    public String name()
    {
        return field.getName();
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
        return field.getType().isArray();
    }

    public String valueToShowInHelp()
    {
        return DEFAULT_STRING;
    }

    public Class<? extends Annotation> annotationType()
    {
        return Argument.class;
    }
}
