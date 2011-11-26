package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.tesla.shell.support.spi.BindingProcessor;
import org.eclipse.tesla.shell.support.spi.FunctionDescriptor;
import org.sonatype.gshell.command.Command;
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
