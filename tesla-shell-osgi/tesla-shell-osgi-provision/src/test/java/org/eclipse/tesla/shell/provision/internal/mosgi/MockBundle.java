package org.eclipse.tesla.shell.provision.internal.mosgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * TODO
 *
 * @since 1.0
 */
public abstract class MockBundle
    implements Bundle
{

    private Properties properties;

    private BundleContext bundleContext;

    private long id;

    private List<String> exports;

    MockBundle setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        return this;
    }

    MockBundle setBundleId( final long id )
    {
        this.id = id;
        return this;
    }

    public MockBundle withBundleSymbolicName( final String bsn )
    {
        getProperties().setProperty( Constants.BUNDLE_SYMBOLICNAME, bsn );
        return this;
    }

    @Override
    public long getBundleId()
    {
        return id;
    }

    @Override
    public BundleContext getBundleContext()
    {
        return bundleContext;
    }

    @Override
    public Dictionary getHeaders()
    {
        return getProperties();
    }



    public MockBundle withPackages( final String... exports )
    {
        this.getExports().addAll( Arrays.asList( exports ) );
        this.properties.setProperty( Constants.EXPORT_PACKAGE, exports() );
        return this;
    }

    private String exports()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final String export : getExports() )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "," );
            }
            sb.append( export );
        }
        return sb.toString();
    }

    private List<String> getExports()
    {
        if(exports == null)
        {
            exports = new ArrayList<String>(  );
        }
        return exports;
    }

    private Properties getProperties()
    {
        if ( properties == null )
        {
            properties = new Properties();
        }
        return properties;
    }

}
