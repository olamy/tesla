/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.util.ResourceBundle;

import org.apache.karaf.shell.commands.Argument;

/**
 * Describes an action argument.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class ArgumentDescriptor
{

    public static final String DEFAULT = Argument.DEFAULT;

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private ActionInjector injector;

    private String name = DEFAULT;

    private String description = "";

    private boolean required;

    private boolean multiValued;

    private int index = 0;

    private String valueToShowInHelp = DEFAULT;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

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

    public ArgumentDescriptor loadDescription( final ResourceBundle resourceBundle,
                                               final String key )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.argument." + key );
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

    public ArgumentDescriptor loadDescription( final ResourceBundle resourceBundle )
    {
        return loadDescription( resourceBundle, getName() );
    }

    public ArgumentDescriptor loadValueToShowInHelp( final ResourceBundle resourceBundle,
                                                     final String key )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.argument." + key + ".details" );
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

    public ArgumentDescriptor loadValueToShowInHelp( final ResourceBundle resourceBundle )
    {
        return loadValueToShowInHelp( resourceBundle, getName() );
    }

}
