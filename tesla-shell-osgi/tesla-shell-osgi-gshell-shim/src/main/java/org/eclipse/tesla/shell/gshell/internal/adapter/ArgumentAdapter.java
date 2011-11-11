package org.eclipse.tesla.shell.gshell.internal.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.felix.gogo.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentAdapter
    implements Argument
{

    private final org.sonatype.gshell.util.cli2.Argument delegate;

    private final Field field;

    public ArgumentAdapter( final org.sonatype.gshell.util.cli2.Argument delegate,
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
        return field.getType().isArray() || Collection.class.isAssignableFrom( field.getType() );
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
