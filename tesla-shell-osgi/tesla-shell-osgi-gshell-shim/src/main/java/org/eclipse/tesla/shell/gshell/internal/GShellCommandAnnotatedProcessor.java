package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.felix.gogo.commands.Command;
import org.eclipse.tesla.shell.gshell.internal.adapter.CommandAdapter;
import org.eclipse.tesla.shell.support.spi.BindingProcessor;
import org.eclipse.tesla.shell.support.spi.FunctionDescriptor;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GShellCommandAnnotatedProcessor
    implements BindingProcessor
{

    public boolean handles( final Class<Object> implementationClass )
    {
        return getAnnotation( implementationClass ) != null
            && CommandAction.class.isAssignableFrom( implementationClass );
    }

    public FunctionDescriptor process( final BeanEntry<Annotation, Object> beanEntry )
    {
        final Command annotation = getAnnotation( beanEntry.getImplementationClass() );
        return new FunctionDescriptor.Default(
            annotation.scope(),
            annotation.name(),
            new GShellShimCommand( annotation, beanEntry )
        );
    }

    private Command getAnnotation( final Class<Object> implementationClass )
    {
        final org.sonatype.gshell.command.Command annotation =
            implementationClass.getAnnotation( org.sonatype.gshell.command.Command.class );
        if ( annotation != null )
        {
            return new CommandAdapter( annotation );
        }
        return null;
    }

}
