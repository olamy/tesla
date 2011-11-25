package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.eclipse.tesla.shell.support.spi.BindingProcessor;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GogoAbstractCommandProcessor
    extends GogoCommandAnnotatedProcessor
    implements BindingProcessor
{

    @Override
    public boolean handles( final Class<Object> implementationClass )
    {
        return super.handles( implementationClass ) && AbstractCommand.class.isAssignableFrom( implementationClass );
    }

    @Override
    protected Function getFunction( final BeanEntry<Annotation, Object> beanEntry )
    {
        return (Function) beanEntry.getProvider().get();
    }

}
