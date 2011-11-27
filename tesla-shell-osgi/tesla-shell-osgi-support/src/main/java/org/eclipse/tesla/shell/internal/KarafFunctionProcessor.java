package org.eclipse.tesla.shell.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.service.command.Function;
import org.eclipse.tesla.shell.spi.BindingProcessor;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class KarafFunctionProcessor
    extends KarafCommandAnnotatedProcessor
    implements BindingProcessor
{

    private final List<BindingProcessor> processors;

    @Inject
    KarafFunctionProcessor( final List<BindingProcessor> processors )
    {
        this.processors = processors;
    }

    @Override
    public boolean handles( final Class<Object> implementationClass )
    {
        if ( !( super.handles( implementationClass ) && Function.class.isAssignableFrom( implementationClass ) ) )
        {
            return false;
        }
        for ( final BindingProcessor processor : processors )
        {
            if ( !( processor instanceof KarafFunctionProcessor )
                && processor.handles( implementationClass ) )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Function getFunction( final BeanEntry<Annotation, Object> beanEntry )
    {
        return (Function) beanEntry.getProvider().get();
    }

}
