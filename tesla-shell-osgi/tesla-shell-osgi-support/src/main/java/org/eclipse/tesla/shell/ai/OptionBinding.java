package org.eclipse.tesla.shell.ai;

import org.apache.felix.gogo.commands.Option;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionBinding
{

    private final Option option;

    private final ActionInjector injector;

    public OptionBinding( final Option option, final ActionInjector injector )
    {
        if ( option == null )
        {
            throw new IllegalArgumentException( "@Option cannot be null" );
        }
        if ( injector == null )
        {
            throw new IllegalArgumentException( "Injector cannot be null" );
        }

        this.option = option;
        this.injector = injector;
    }

    OptionBinding( final Option option )
    {
        if ( option == null )
        {
            throw new IllegalArgumentException( "@Option cannot be null" );
        }
        this.option = option;
        this.injector = null;
    }

    public Option getOption()
    {
        return option;
    }

    public ActionInjector getInjector()
    {
        return injector;
    }
}
