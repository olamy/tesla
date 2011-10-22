package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.eclipse.tesla.shell.support.spi.CommandAnnotationProcessor;
import org.eclipse.tesla.shell.support.spi.ShellCommand;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GogoCommandAnnotationProcessor
    implements CommandAnnotationProcessor
{

    public Command getAnnotation( final Class<Object> implementationClass )
    {
        return implementationClass.getAnnotation( Command.class );
    }

    public boolean handles( final Class<Object> implementationClass )
    {
        return getAnnotation( implementationClass ) != null
            && Action.class.isAssignableFrom( implementationClass );
    }

    public ShellCommand createCommand( final Command commandAnnotation, final BeanEntry<Annotation, Object> beanEntry )
    {
        return new ShellCommand(commandAnnotation,beanEntry );
    }

}
