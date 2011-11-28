/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.spi;

import java.lang.annotation.Annotation;

import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
public interface BindingProcessor
{

    boolean handles( Class<Object> implementationClass );

    FunctionDescriptor process( BeanEntry<Annotation, Object> beanEntry );

}
