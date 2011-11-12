package org.sonatype.maven.shell.maven.internal;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.sonatype.guice.bean.reflect.BundleClassSpace;
import org.sonatype.guice.plexus.binders.PlexusXmlBeanModule;
import org.sonatype.guice.plexus.config.PlexusBeanModule;
import org.sonatype.maven.shell.maven.MavenRuntimeConfiguration;

/**
 * TODO
 *
 * @since 1.0
 */
public class OsgiBundleSpaceDelegate
    implements MavenRuntimeConfiguration.Delegate
{

    private BundleContext bundleContext;

    @Inject
    void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void configure( final DefaultPlexusContainer container )
        throws Exception
    {
        for ( final Bundle bundle : bundleContext.getBundles() )
        {
            if ( bundle.getBundleId() != 0 )
            {
                final List<PlexusBeanModule> beanModules = new ArrayList<PlexusBeanModule>();
                final ContextMapAdapter variables = new ContextMapAdapter( container.getContext() );
                final BundleClassSpace space = new BundleClassSpace( bundle );
                beanModules.add( new PlexusXmlBeanModule( space, variables ) );
                container.addPlexusInjector( beanModules );
            }
        }
    }

}
