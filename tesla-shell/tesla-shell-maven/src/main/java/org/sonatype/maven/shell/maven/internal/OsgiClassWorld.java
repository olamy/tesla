package org.sonatype.maven.shell.maven.internal;

import javax.inject.Inject;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.osgi.framework.BundleContext;

/**
 * TODO
 *
 * @since 1.0
 */
public class OsgiClassWorld
{

    private BundleContext bundleContext;

    @Inject
    void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public ClassWorld getClassWorld()
        throws Exception
    {
        return new ClassWorld( "plexus.core", new BundleClassLoader( bundleContext.getBundle() ) );
    }

}
