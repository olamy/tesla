package org.eclipse.tesla.shell.preparator;

import org.apache.karaf.shell.commands.Option;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionDescriptor
{

    public static final String DEFAULT = Option.DEFAULT_STRING;

    private ActionInjector injector;

    private String name;

    private String[] aliases = { };

    private boolean multiValued;

    private boolean required;

    private String description = "";

    private String valueToShowInHelp = DEFAULT;

    public ActionInjector getInjector()
    {
        return injector;
    }

    public OptionDescriptor setInjector( final ActionInjector injector )
    {
        this.injector = injector;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public OptionDescriptor setName( final String name )
    {
        this.name = name;
        return this;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public OptionDescriptor setAliases( final String... aliases )
    {
        this.aliases = aliases;
        return this;
    }

    public boolean isMultiValued()
    {
        return multiValued;
    }

    public OptionDescriptor setMultiValued( final boolean multiValued )
    {
        this.multiValued = multiValued;
        return this;
    }

    public boolean isRequired()
    {
        return required;
    }

    public OptionDescriptor setRequired( final boolean required )
    {
        this.required = required;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public OptionDescriptor setDescription( final String description )
    {
        this.description = description;
        return this;
    }

    public String getValueToShowInHelp()
    {
        return valueToShowInHelp;
    }

    public OptionDescriptor setValueToShowInHelp( final String valueToShowInHelp )
    {
        this.valueToShowInHelp = valueToShowInHelp;
        return this;
    }

}
