package org.sonatype.maven.shell.maven.internal;

import javax.inject.Inject;
import javax.inject.Named;

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
        throws Exception
    {
        try
        {
            final OsgiBundleSpaceDelegate spaceDelegate = new OsgiBundleSpaceDelegate();
            injector.injectMembers( spaceDelegate );
            configuration.setDelegate( spaceDelegate );

            final OsgiClassWorld classWorldSource = new OsgiClassWorld();
            injector.injectMembers( classWorldSource );
            configuration.setClassWorld( classWorldSource.getClassWorld() );
        }
        catch ( NoClassDefFoundError e )
        {
            // ignore
        }
    }

}
