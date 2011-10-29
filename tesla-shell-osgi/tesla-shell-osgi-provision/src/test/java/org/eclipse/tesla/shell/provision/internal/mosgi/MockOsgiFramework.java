package org.eclipse.tesla.shell.provision.internal.mosgi;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * TODO
 *
 * @since 1.0
 */
public class MockOsgiFramework
{

    private List<Bundle> bundles;

    private BundleContext bundleContext;

    public MockOsgiFramework()
    {
        bundles = new ArrayList<Bundle>();
        bundleContext = mock( BundleContext.class );
        when( bundleContext.getBundles() ).thenReturn( bundles.toArray( new Bundle[bundles.size()] ) );
        when( bundleContext.getBundle( anyLong() ) ).thenAnswer(
            new Answer<Bundle>()
            {
                @Override
                public Bundle answer( final InvocationOnMock invocation )
                    throws Throwable
                {
                    return bundles.get( ( (Long) invocation.getArguments()[0] ).intValue() );
                }
            }
        );
        try
        {
            when( bundleContext.installBundle( anyString() ) ).thenAnswer(
                new Answer<Bundle>()
                {
                    @Override
                    public Bundle answer( final InvocationOnMock invocation )
                        throws Throwable
                    {
                        return installBundle();
                    }
                }
            );
            when( bundleContext.installBundle( anyString(), Mockito.<InputStream>any() ) ).thenThrow(
                UnsupportedOperationException.class
            );
        }
        catch ( BundleException ignore )
        {
            // ignore
        }

        installBundle().withBundleSymbolicName( "system" );
    }

    public BundleContext getBundleContext()
    {
        return bundleContext;
    }

    public MockBundle installBundle()
    {
        final MockBundle bundle = mock( MockBundle.class, new PartialImplementation( MockBundle.class ) );
        bundles.add( bundle );
        return bundle.setBundleId( bundles.size() - 1 ).setBundleContext( getBundleContext() );
    }

}
