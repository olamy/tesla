package org.eclipse.tesla.shell.gshell.internal.adapter;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.NullCompleter;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionAdapter
    implements Option
{

    private org.sonatype.gshell.util.cli2.Option delegate;

    public OptionAdapter( final org.sonatype.gshell.util.cli2.Option delegate )
    {
        this.delegate = delegate;
    }

    public String name()
    {
        if ( delegate.name() == null || "__EMPTY__".equals( delegate.name() ) )
        {
            return "--" + delegate.longName();
        }
        return "-" + delegate.name();
    }

    public String[] aliases()
    {
        if ( delegate.name() == null || "__EMPTY__".equals( delegate.name() )
            || delegate.longName() == null || "__EMPTY__".equals( delegate.longName() ) )
        {
            return new String[0];
        }
        return new String[]{ "--" + delegate.longName() };
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

    public Class<? extends Completer> completer()
    {
        return NullCompleter.class;
    }

    public String valueToShowInHelp()
    {
        return DEFAULT_STRING;
    }

    public Class<? extends Annotation> annotationType()
    {
        return Option.class;
    }

    @Override
    public String toString()
    {
        return String.format( "%s (aliases=%s, required=%s, multiple-values=%s)",
                              name(), Arrays.toString( aliases() ), required(), multiValued()
        );
    }
}
