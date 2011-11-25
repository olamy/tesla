package org.eclipse.tesla.shell.preparator;

/**
 * TODO
 *
 * @since 1.0
 */
public class CommandDescriptor
{

    private String scope;

    private String name;

    private String description = "";

    private String detailedDescription = "";

    public String getScope()
    {
        return scope;
    }

    public CommandDescriptor setScope( final String scope )
    {
        this.scope = scope;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public CommandDescriptor setName( final String name )
    {
        this.name = name;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public CommandDescriptor setDescription( final String description )
    {
        this.description = description;
        return this;
    }

    public String getDetailedDescription()
    {
        return detailedDescription;
    }

    public CommandDescriptor setDetailedDescription( final String detailedDescription )
    {
        this.detailedDescription = detailedDescription;
        return this;
    }

}
