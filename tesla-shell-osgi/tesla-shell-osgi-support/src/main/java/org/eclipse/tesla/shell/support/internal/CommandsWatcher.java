package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.support.spi.CommandAnnotationProcessor;
import org.eclipse.tesla.shell.support.spi.ShellCommand;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.guice.bean.locators.BeanLocator;
import org.sonatype.inject.BeanEntry;
import org.sonatype.inject.EagerSingleton;
import org.sonatype.inject.Mediator;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@EagerSingleton
class CommandsWatcher
{

    private BundleContext bundleContext;

    private Map<BeanEntry<Annotation, Object>, ServiceRegistration> commands;

    private BeanLocator beanLocator;

    private List<CommandAnnotationProcessor> processors;

    @Inject
    CommandsWatcher( final BundleContext bundleContext,
                     final BeanLocator beanLocator,
                     final List<CommandAnnotationProcessor> processors )
    {
        this.beanLocator = beanLocator;
        this.processors = processors;
        commands = new HashMap<BeanEntry<Annotation, Object>, ServiceRegistration>();
        this.bundleContext = bundleContext;
        beanLocator.watch(
            Key.get( Object.class ),
            new Mediator<Annotation, Object, CommandsWatcher>()
            {
                public void add( final BeanEntry<Annotation, Object> beanEntry, final CommandsWatcher watcher )
                    throws Exception
                {
                    watcher.registerCommand( beanEntry );
                }

                public void remove( final BeanEntry<Annotation, Object> beanEntry, final CommandsWatcher watcher )
                    throws Exception
                {
                    watcher.unregisterCommand( beanEntry );
                }
            },
            this
        );
    }

    private void registerCommand( final BeanEntry<Annotation, Object> beanEntry )
    {
        final Class<Object> implementationClass = beanEntry.getImplementationClass();
        final CommandAnnotationProcessor processor = getAnnotationProcessor( implementationClass );
        if ( processor != null )
        {
            final Command commandAnnotation = processor.getAnnotation( implementationClass );
            final ShellCommand command = processor.createCommand( commandAnnotation, beanEntry );
            final Properties commandProperties = new Properties();
            commandProperties.setProperty( "osgi.command.scope", command.getScope() );
            commandProperties.setProperty( "osgi.command.function", command.getName() );
            commandProperties.setProperty( "implementationClass", implementationClass.getName() );
            final BundleContext commandBundleContext = bundleContextOfCommand( implementationClass );
            if ( commandBundleContext != null )
            {
                final ServiceRegistration serviceRegistration = commandBundleContext.registerService(
                    new String[]{ Function.class.getName(), CompletableFunction.class.getName() },
                    command,
                    commandProperties
                );
                commands.put( beanEntry, serviceRegistration );
            }
        }
    }

    private BundleContext bundleContextOfCommand( final Class<Object> implementationClass )
    {
        for ( final BeanEntry<Annotation, Injector> beanEntry : beanLocator.locate( Key.get( Injector.class ) ) )
        {
            final Injector injector = beanEntry.getValue();
            try
            {
                injector.getBinding( implementationClass );
                // if we passed above it means that injector is the one for the implementation class
                return injector.getInstance( BundleContext.class );
            }
            catch ( ConfigurationException ignore )
            {
            }
        }
        return null;
    }

    private CommandAnnotationProcessor getAnnotationProcessor( final Class<Object> implementationClass )
    {
        for ( final CommandAnnotationProcessor processor : processors )
        {
            if ( processor.handles( implementationClass ) )
            {
                return processor;
            }
        }
        return null;
    }

    private void unregisterCommand( final BeanEntry<Annotation, Object> beanEntry )
    {
        final ServiceRegistration serviceRegistration = commands.remove( beanEntry );
        if ( serviceRegistration != null )
        {
            serviceRegistration.unregister();
        }
    }

}
