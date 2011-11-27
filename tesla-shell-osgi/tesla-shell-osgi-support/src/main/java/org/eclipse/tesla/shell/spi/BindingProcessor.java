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
