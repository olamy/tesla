/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.tesla.shell.preparator;

/**
 * Describes an action command.
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
