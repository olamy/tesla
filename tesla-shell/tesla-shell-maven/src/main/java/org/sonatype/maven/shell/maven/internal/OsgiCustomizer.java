package org.sonatype.maven.shell.maven.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.framework.BundleContext;
import org.sonatype.maven.shell.maven.MavenRuntimeConfiguration;
import com.google.inject.Injector;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class OsgiCustomizer
    implements MavenRuntimeConfiguration.Customizer
{

    private final Injector injector;

    @Inject
    OsgiCustomizer( final Injector injector )
    {
        this.injector = injector;
    }

    public void customize( final MavenRuntimeConfiguration configuration )
    {
        if ( isAnOSGiEnvironment() )
        {
            final OsgiBundleSpaceDelegate spaceDelegate = injector.getInstance( OsgiBundleSpaceDelegate.class );
            configuration.setDelegate( spaceDelegate );

            final OsgiClassWorld classWorldSource = injector.getInstance( OsgiClassWorld.class );
            configuration.setClassWorld( classWorldSource.getClassWorld() );
        }
    }

    private boolean isAnOSGiEnvironment()
    {
        try
        {
            return injector.getBinding( BundleContext.class ) != null;
        }
        catch ( NoClassDefFoundError ignore )
        {
            return false;
        }
    }

}
