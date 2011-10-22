package org.eclipse.tesla.shell.support.spi;

import java.lang.annotation.Annotation;

import org.apache.felix.gogo.commands.Command;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
public interface CommandAnnotationProcessor
{

    Command getAnnotation( Class<Object> implementationClass );

    boolean handles( Class<Object> implementationClass );

    ShellCommand createCommand( Command commandAnnotation, BeanEntry<Annotation, Object> beanEntry );
}
