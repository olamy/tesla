package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.Function;
import org.eclipse.tesla.shell.support.spi.BindingProcessor;
import org.eclipse.tesla.shell.support.spi.FunctionDescriptor;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GogoActionProcessor
    extends GogoCommandAnnotatedProcessor
    implements BindingProcessor
{

    @Override
    public boolean handles( final Class<Object> implementationClass )
    {
        return super.handles( implementationClass ) && Action.class.isAssignableFrom( implementationClass );
    }

    @Override
    protected Function getFunction( final BeanEntry<Annotation, Object> beanEntry )
    {
        return new ActionProxy( beanEntry );
    }

}
