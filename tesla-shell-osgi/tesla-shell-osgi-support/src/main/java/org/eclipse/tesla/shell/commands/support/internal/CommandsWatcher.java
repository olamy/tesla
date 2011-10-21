package org.eclipse.tesla.shell.commands.support.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.sonatype.guice.bean.locators.BeanLocator;
import org.sonatype.inject.BeanEntry;
import org.sonatype.inject.EagerSingleton;
import org.sonatype.inject.Mediator;
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

    private Map<BeanEntry<Annotation, Action>, ServiceRegistration> commands;

    @Inject
    CommandsWatcher( final BundleContext bundleContext,
                     final BeanLocator beanLocator )
    {
        commands = new HashMap<BeanEntry<Annotation, Action>, ServiceRegistration>();
        this.bundleContext = bundleContext;
        beanLocator.watch(
            Key.get( Action.class ),
            new Mediator<Annotation, Action, CommandsWatcher>()
            {
                public void add( final BeanEntry<Annotation, Action> beanEntry, final CommandsWatcher watcher )
                    throws Exception
                {
                    watcher.registerCommand( beanEntry );
                }

                public void remove( final BeanEntry<Annotation, Action> beanEntry, final CommandsWatcher watcher )
                    throws Exception
                {
                    watcher.unregisterCommand( beanEntry );
                }
            },
            this
        );
    }

    private void registerCommand( final BeanEntry<Annotation, Action> beanEntry )
    {
        final Class<Action> implementationClass = beanEntry.getImplementationClass();
        final Command commandAnnotation = implementationClass.getAnnotation( Command.class );

        if ( commandAnnotation != null )
        {
            final GuiceShellCommand command = new GuiceShellCommand( commandAnnotation, beanEntry );
            final Properties commandProperties = new Properties();
            commandProperties.setProperty( "osgi.command.scope", command.getScope() );
            commandProperties.setProperty( "osgi.command.function", command.getName() );
            commandProperties.setProperty( "implementationClass", implementationClass.getName() );
            final ServiceRegistration serviceRegistration = bundleContext.registerService(
                new String[]{ Function.class.getName(), CompletableFunction.class.getName() },
                command,
                commandProperties
            );
            commands.put( beanEntry, serviceRegistration );
        }
    }

    private void unregisterCommand( final BeanEntry<Annotation, Action> beanEntry )
    {
        final ServiceRegistration serviceRegistration = commands.remove( beanEntry );
        if ( serviceRegistration != null )
        {
            serviceRegistration.unregister();
        }
    }

}
