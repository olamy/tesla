/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.spi;

import org.apache.felix.service.command.Function;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public interface FunctionDescriptor
{

    String getScope();

    String getName();

    Function getFunction();

    static class Default
        implements FunctionDescriptor
    {

        private final String scope;

        private final String name;

        private final Function function;

        public Default( final String scope, final String name, final Function function )
        {
            this.scope = scope;
            this.name = name;
            this.function = function;
        }

        public Function getFunction()
        {
            return function;
        }

        public String getName()
        {
            return name;
        }

        public String getScope()
        {
            return scope;
        }

    }

}
