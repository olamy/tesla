/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.util.ResourceBundle;

import org.apache.karaf.shell.commands.Option;

/**
 * Describes an action option.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class OptionDescriptor
{

    public static final String DEFAULT = Option.DEFAULT_STRING;

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private ActionInjector injector;

    private String name;

    private String[] aliases = { };

    private boolean multiValued;

    private boolean required;

    private String description = "";

    private String valueToShowInHelp = DEFAULT;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

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

    public OptionDescriptor loadDescription( final ResourceBundle resourceBundle,
                                             final String key )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.option." + key );
                if ( rbDescription != null && rbDescription.trim().length() > 0 )
                {
                    setDescription( rbDescription );
                }
            }
            catch ( Exception e )
            {
                // ignore
            }
        }
        return this;
    }

    public OptionDescriptor loadDescription( final ResourceBundle resourceBundle )
    {
        String key = getName();
        if ( key.startsWith( "--" ) )
        {
            key = key.substring( 2 );
        }
        else if ( key.startsWith( "-" ) )
        {
            key = key.substring( 1 );
        }
        return loadDescription( resourceBundle, key );
    }

    public OptionDescriptor loadValueToShowInHelp( final ResourceBundle resourceBundle,
                                                   final String key )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.option." + key + ".details" );
                if ( rbDescription != null && rbDescription.trim().length() > 0 )
                {
                    setDescription( rbDescription );
                }
            }
            catch ( Exception e )
            {
                // ignore
            }
        }
        return this;
    }

    public OptionDescriptor loadValueToShowInHelp( final ResourceBundle resourceBundle )
    {
        String key = getName();
        if ( key.startsWith( "--" ) )
        {
            key = key.substring( 2 );
        }
        else if ( key.startsWith( "-" ) )
        {
            key = key.substring( 1 );
        }
        return loadValueToShowInHelp( resourceBundle, key );
    }

}
