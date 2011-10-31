package org.eclipse.tesla.shell.provision.internal.mosgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * TODO
 *
 * @since 1.0
 */
public class PackageUtils
{

    public static String[] packagesFrom( final File file )
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream( file );
            return packagesFrom( in );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            IOUtils.close( in );
        }
    }

    public static String[] packagesFrom( final URL url )
    {
        InputStream in = null;
        try
        {
            in = url.openStream();
            return packagesFrom( in );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            IOUtils.close( in );
        }
    }

    public static String[] packagesFrom( final InputStream stream )
    {
        try
        {
            final Properties properties = new Properties();
            properties.load( stream );
            return packagesFrom( properties );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String[] packagesFrom( final Properties properties )
    {
        final String packages = properties.getProperty( "packages" );
        return packages.split( "," );
    }

    public static String[] packagesOf( final ExecutionEnvironment environment )
    {
        final URL resource = PackageUtils.class.getClassLoader().getResource( environment.getValue() + ".properties" );
        return packagesFrom( resource );
    }

    public static String[] packagesOf( final OSGiFramework framework )
    {
        final URL resource = PackageUtils.class.getClassLoader().getResource( framework.getValue() + ".properties" );
        return packagesFrom( resource );
    }

}
