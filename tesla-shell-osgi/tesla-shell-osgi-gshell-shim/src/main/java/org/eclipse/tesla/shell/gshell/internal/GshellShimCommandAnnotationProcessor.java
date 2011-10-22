package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.gogo.commands.Command;
import org.eclipse.tesla.shell.support.spi.CommandAnnotationProcessor;
import org.eclipse.tesla.shell.support.spi.ShellCommand;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GshellShimCommandAnnotationProcessor
    implements CommandAnnotationProcessor
{

    public Command getAnnotation( final Class<Object> implementationClass )
    {
        final org.sonatype.gshell.command.Command annotation =
            implementationClass.getAnnotation( org.sonatype.gshell.command.Command.class );
        if ( annotation != null )
        {
            return new Command()
            {
                public String scope()
                {
                    return "shim";
                }

                public String name()
                {
                    return annotation.name();
                }

                public String description()
                {
                    return "";
                }

                public String detailedDescription()
                {
                    return "";
                }

                public Class<? extends Annotation> annotationType()
                {
                    return Command.class;
                }
            };
        }
        return null;
    }

    public boolean handles( final Class<Object> implementationClass )
    {
        return getAnnotation( implementationClass ) != null
            && CommandAction.class.isAssignableFrom( implementationClass );
    }

    public ShellCommand createCommand( final Command commandAnnotation, final BeanEntry<Annotation, Object> beanEntry )
    {
        return new GShellShimShellCommand( commandAnnotation, beanEntry );
    }

}
