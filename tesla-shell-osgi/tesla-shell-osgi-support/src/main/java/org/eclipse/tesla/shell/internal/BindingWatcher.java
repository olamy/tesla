package org.eclipse.tesla.shell.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.spi.BindingProcessor;
import org.eclipse.tesla.shell.spi.FunctionDescriptor;
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
class BindingWatcher
{

    private Map<BeanEntry<Annotation, Object>, ServiceRegistration> commands;

    private BeanLocator beanLocator;

    private List<BindingProcessor> processors;

    @Inject
    BindingWatcher( final BeanLocator beanLocator,
                    final List<BindingProcessor> processors )
    {
        this.beanLocator = beanLocator;
        this.processors = processors;
        commands = new HashMap<BeanEntry<Annotation, Object>, ServiceRegistration>();
        beanLocator.watch(
            Key.get( Object.class ),
            new Mediator<Annotation, Object, BindingWatcher>()
            {
                public void add( final BeanEntry<Annotation, Object> beanEntry, final BindingWatcher watcher )
                    throws Exception
                {
                    watcher.registerFunction( beanEntry );
                }

                public void remove( final BeanEntry<Annotation, Object> beanEntry, final BindingWatcher watcher )
                    throws Exception
                {
                    watcher.unregisterFunction( beanEntry );
                }
            },
            this
        );
    }

    private void registerFunction( final BeanEntry<Annotation, Object> beanEntry )
    {
        final Class<Object> implementationClass = beanEntry.getImplementationClass();
        final BindingProcessor processor = getBindingProcessor( implementationClass );
        if ( processor != null )
        {
            final FunctionDescriptor descriptor = processor.process( beanEntry );
            final Properties commandProperties = new Properties();
            commandProperties.setProperty( "osgi.command.scope", descriptor.getScope() );
            commandProperties.setProperty( "osgi.command.function", descriptor.getName() );
            commandProperties.setProperty( "implementationClass", implementationClass.getName() );
            final BundleContext commandBundleContext = bundleContextOfCommand( implementationClass );
            if ( commandBundleContext != null )
            {
                final ServiceRegistration serviceRegistration = commandBundleContext.registerService(
                    new String[]{ Function.class.getName(), CompletableFunction.class.getName() },
                    descriptor.getFunction(),
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

    private BindingProcessor getBindingProcessor( final Class<Object> implementationClass )
    {
        for ( final BindingProcessor processor : processors )
        {
            if ( processor.handles( implementationClass ) )
            {
                return processor;
            }
        }
        return null;
    }

    private void unregisterFunction( final BeanEntry<Annotation, Object> beanEntry )
    {
        final ServiceRegistration serviceRegistration = commands.remove( beanEntry );
        if ( serviceRegistration != null )
        {
            try
            {
                serviceRegistration.unregister();
            }
            catch ( Exception ignore )
            {
                // ignore, as service could have been already unregistered because, for example, bundle was uninstalled
            }
        }
    }

}
