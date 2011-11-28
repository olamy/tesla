/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.tesla.shell.spi.BindingProcessor;
import org.eclipse.tesla.shell.spi.FunctionDescriptor;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.inject.BeanEntry;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
@Singleton
public class GShellCommandAnnotatedProcessor
    implements BindingProcessor
{

    static final String SHIM = "shim";

    public boolean handles( final Class<Object> implementationClass )
    {
        return getAnnotation( implementationClass ) != null
            && CommandAction.class.isAssignableFrom( implementationClass );
    }

    public FunctionDescriptor process( final BeanEntry<Annotation, Object> beanEntry )
    {
        final Command annotation = getAnnotation( beanEntry.getImplementationClass() );
        final String[] segments = beanEntry.getImplementationClass().getPackage().getName().split( "\\." );
        return new FunctionDescriptor.Default(
            segments.length > 0 ? segments[segments.length - 1] : SHIM,
            annotation.name(),
            new GShellShimCommand( beanEntry )
        );
    }

    private Command getAnnotation( final Class<Object> implementationClass )
    {
        return implementationClass.getAnnotation( Command.class );
    }

}
