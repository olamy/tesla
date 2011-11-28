/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.commands.Command;
import org.eclipse.tesla.shell.spi.BindingProcessor;
import org.eclipse.tesla.shell.spi.FunctionDescriptor;
import org.sonatype.inject.BeanEntry;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
@Singleton
public abstract class KarafCommandAnnotatedProcessor
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
            getFunction( beanEntry )
        );
    }

    protected abstract Function getFunction( final BeanEntry<Annotation, Object> beanEntry );

    private Command getAnnotation( final Class<Object> implementationClass )
    {
        return implementationClass.getAnnotation( Command.class );
    }

}
