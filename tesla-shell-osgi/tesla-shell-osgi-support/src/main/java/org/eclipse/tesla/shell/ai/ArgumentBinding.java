package org.eclipse.tesla.shell.ai;

import org.apache.felix.gogo.commands.Argument;

/**
 * TODO
 *
 * @since 1.0
 */
public class ArgumentBinding
{

    private final Argument argument;

    private final ActionInjector injector;

    public ArgumentBinding( final Argument argument, final ActionInjector injector )
    {
        if ( argument == null )
        {
            throw new IllegalArgumentException( "@Argument cannot be null" );
        }
        if ( injector == null )
        {
            throw new IllegalArgumentException( "Injector cannot be null" );
        }
        this.argument = argument;
        this.injector = injector;
    }

    public Argument getArgument()
    {
        return argument;
    }

    public ActionInjector getInjector()
    {
        return injector;
    }

}
