package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

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
public abstract class GogoCommandAnnotatedProcessor
    implements BindingProcessor
{

    public boolean handles( final Class<Object> implementationClass )
    {
        return getAnnotation( implementationClass ) != null;
    }

    public FunctionDescriptor process( final BeanEntry<Annotation, Object> beanEntry )
    {
        final Command annotation = getAnnotation( beanEntry.getImplementationClass() );
        return new FunctionDescriptor.Default(
            annotation.scope(),
            annotation.name(),
            getFunction(beanEntry)
        );
    }

    protected abstract Function getFunction( final BeanEntry<Annotation, Object> beanEntry );

    private Command getAnnotation( final Class<Object> implementationClass )
    {
        return implementationClass.getAnnotation( Command.class );
    }

}
