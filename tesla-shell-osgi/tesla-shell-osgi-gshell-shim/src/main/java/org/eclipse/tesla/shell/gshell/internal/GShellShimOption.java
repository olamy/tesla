package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;

import org.apache.felix.gogo.commands.Option;

/**
* TODO
*
* @since 1.0
*/
class GShellShimOption
    implements Option
{

    private org.sonatype.gshell.util.cli2.Option delegate;

    GShellShimOption( final org.sonatype.gshell.util.cli2.Option delegate )
    {
        this.delegate = delegate;
    }

    public String name()
    {
        return delegate.name();
    }

    public String[] aliases()
    {
        if ( delegate.longName() != null )
        {
            return new String[]{ delegate.longName() };
        }
        return new String[0];
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
        return false;
    }

    public String valueToShowInHelp()
    {
        return DEFAULT_STRING;
    }

    public Class<? extends Annotation> annotationType()
    {
        return Option.class;
    }
}
