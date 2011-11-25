package org.eclipse.tesla.shell.preparator;

import org.apache.karaf.shell.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentDescriptor
{

    public static final String DEFAULT = Argument.DEFAULT;

    private ActionInjector injector;

    private String name = DEFAULT;

    private String description = "";

    private boolean required;

    private boolean multiValued;

    private int index = 0;

    private String valueToShowInHelp = DEFAULT;

    public ActionInjector getInjector()
    {
        return injector;
    }

    public ArgumentDescriptor setInjector( final ActionInjector injector )
    {
        this.injector = injector;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ArgumentDescriptor setName( final String name )
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public ArgumentDescriptor setDescription( final String description )
    {
        this.description = description;
        return this;
    }

    public boolean isRequired()
    {
        return required;
    }

    public ArgumentDescriptor setRequired( final boolean required )
    {
        this.required = required;
        return this;
    }

    public boolean isMultiValued()
    {
        return multiValued;
    }

    public ArgumentDescriptor setMultiValued( final boolean multiValued )
    {
        this.multiValued = multiValued;
        return this;
    }

    public int getIndex()
    {
        return index;
    }

    public ArgumentDescriptor setIndex( final int index )
    {
        this.index = index;
        return this;
    }

    public String getValueToShowInHelp()
    {
        return valueToShowInHelp;
    }

    public ArgumentDescriptor setValueToShowInHelp( final String valueToShowInHelp )
    {
        this.valueToShowInHelp = valueToShowInHelp;
        return this;
    }
}
