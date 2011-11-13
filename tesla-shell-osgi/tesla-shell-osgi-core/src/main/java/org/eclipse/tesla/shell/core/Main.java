package org.eclipse.tesla.shell.core;

import static org.eclipse.tesla.shell.core.internal.PropertiesHelper.loadPropertiesFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.tesla.shell.core.internal.PropertiesHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * TODO
 *
 * @since 1.0
 */
public class Main
{

    public static void main( final String[] args )
        throws Exception
    {
        new Main().boot();
    }

    private void boot()
        throws Exception
    {
        final File etc4tsh = new File( System.getProperty( "shell.home" ), "etc/tsh" );
        final File initialBundles = new File( System.getProperty( "shell.home" ), "lib/shell/bundles" );

        System.setProperty( "logback.configurationFile", new File( etc4tsh, "logback.xml" ).getAbsolutePath() );

        final Properties properties = loadProperties( etc4tsh );

        final Framework framework = loadFramework( properties );
        framework.init();

        provisionFromAssembly( initialBundles, framework.getBundleContext() );

        Thread.currentThread().setContextClassLoader( null );
        framework.start();
        provisionFromStartup( new File( etc4tsh, "startup" ), framework.getBundleContext() );

        while ( true )
        {
            final FrameworkEvent event = framework.waitForStop( 0 );
            if ( event.getType() != FrameworkEvent.STOPPED_UPDATE )
            {
                break;
            }
        }
    }

    private void provisionFromStartup( final File startup, final BundleContext bundleContext )
    {
        try
        {
            final Properties properties = loadPropertiesFile( startup.getParentFile(), startup.getName(), true );

            bundleContext.registerService(
                String.class.getName(),
                Main.class.getName(),
                properties
            );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void provisionFromAssembly( final File bundlesDir, final BundleContext bundleContext )
        throws Exception
    {
        final Collection<Bundle> bundles = new ArrayList<Bundle>();

        for ( final File startupBundle : findAssemblyBundles( bundlesDir ) )
        {
            final Bundle bundle = bundleContext.installBundle( startupBundle.toURI().toASCIIString() );
            bundles.add( bundle );
        }

        for ( Bundle bundle : bundles )
        {
            final String fragmentHostHeader = (String) bundle.getHeaders().get( Constants.FRAGMENT_HOST );
            if ( fragmentHostHeader == null || fragmentHostHeader.trim().length() == 0 )
            {
                bundle.start();
            }
        }
    }

    // @TestAccessible
    static File[] findAssemblyBundles( final File startup )
        throws Exception
    {
        if ( startup.exists() && startup.isDirectory() )
        {
            return startup.listFiles( new FilenameFilter()
            {

                @Override
                public boolean accept( final File file, final String name )
                {
                    return name.endsWith( ".jar" );
                }

            } );
        }
        else
        {
            return new File[0];
        }
    }

    private Framework loadFramework( final Properties properties )
        throws Exception
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final InputStream is = classLoader.getResourceAsStream(
            "META-INF/services/" + FrameworkFactory.class.getName()
        );
        final BufferedReader br = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
        final String factoryClass = br.readLine();
        br.close();
        final FrameworkFactory factory = (FrameworkFactory) classLoader.loadClass( factoryClass ).newInstance();
        return factory.newFramework( properties );
    }

    private Properties loadProperties( final File etc )
        throws Exception
    {
        final Properties properties = loadPropertiesFile( etc, "tsh.properties", false );
        PropertiesHelper.substituteVariables( properties );
        if ( properties.getProperty( Constants.FRAMEWORK_STORAGE ) == null )
        {
            File storage = new File( System.getProperty( "user.home" ), ".m2/tsh/cache" );
            if ( !storage.exists() && !storage.mkdirs() )
            {
                throw new RuntimeException(
                    "Could not create shell caching directory: " + storage.getAbsolutePath()
                );
            }
            properties.setProperty( Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath() );
        }

        return properties;
    }

}

