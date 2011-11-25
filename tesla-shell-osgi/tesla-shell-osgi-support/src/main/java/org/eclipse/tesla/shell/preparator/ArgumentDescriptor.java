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
