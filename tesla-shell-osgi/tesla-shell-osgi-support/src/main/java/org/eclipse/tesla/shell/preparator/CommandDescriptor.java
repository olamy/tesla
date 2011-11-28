/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.preparator;

import java.util.ResourceBundle;

/**
 * Describes an action command.
 *
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class CommandDescriptor
{

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private String scope;

    private String name;

    private String description = "";

    private String detailedDescription = "";

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

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

    public CommandDescriptor loadDescription( final ResourceBundle resourceBundle )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.description" );
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

    public CommandDescriptor loadDetailedDescription( final ResourceBundle resourceBundle )
    {
        if ( resourceBundle != null )
        {
            try
            {
                final String rbDescription = resourceBundle.getString( "command.description.details" );
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

}
