package org.eclipse.tesla.shell.provision.internal.mosgi;

import java.util.Dictionary;
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

    private Properties getProperties()
    {
        if ( properties == null )
        {
            properties = new Properties();
        }
        return properties;
    }

}
