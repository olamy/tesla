package org.eclipse.tesla.shell.ai;

import java.lang.annotation.Annotation;

import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.Completer;

/**
 * TODO
 *
 * @since 1.0
 */
public class HelpOption
    implements Option
{

    public static final HelpOption INSTANCE = new HelpOption();

    private HelpOption()
    {
        // we have a singleton instance
    }

    public String name()
    {
        return "-h";
    }

    public String[] aliases()
    {
        return new String[]{ "--help" };
    }

    public String description()
    {
        return "Display this help message";
    }

    public boolean required()
    {
        return false;
    }

    public boolean multiValued()
    {
        return false;
    }

    public Class<? extends Completer> completer()
    {
        return null;
    }

    public String valueToShowInHelp()
    {
        return Option.DEFAULT_STRING;
    }

    public Class<? extends Annotation> annotationType()
    {
        return Option.class;
    }

}
