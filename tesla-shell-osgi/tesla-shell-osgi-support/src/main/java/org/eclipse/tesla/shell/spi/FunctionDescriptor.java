package org.eclipse.tesla.shell.spi;

import org.apache.felix.service.command.Function;

/**
 * TODO
 *
 * @since 1.0
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

        public Default(final String scope, final String name, final Function function)
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
