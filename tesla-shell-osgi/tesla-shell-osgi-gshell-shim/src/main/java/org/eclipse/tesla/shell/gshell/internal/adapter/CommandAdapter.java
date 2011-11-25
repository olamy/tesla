package org.eclipse.tesla.shell.gshell.internal.adapter;

import java.lang.annotation.Annotation;

import org.apache.karaf.shell.commands.Command;

/**
 * TODO
 *
 * @since 1.0
 */
public class CommandAdapter
    implements Command
{

    private final org.sonatype.gshell.command.Command annotation;

    public CommandAdapter( final org.sonatype.gshell.command.Command annotation )
    {
        this.annotation = annotation;
    }

    public String scope()
    {
        return "shim";
    }

    public String name()
    {
        return annotation.name();
    }

    public String description()
    {
        return "";
    }

    public String detailedDescription()
    {
        return "";
    }

    public Class<? extends Annotation> annotationType()
    {
        return Command.class;
    }
}
