package org.eclipse.tesla.shell.provision.internal.mosgi;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * TODO
 *
 * @since 1.0
 */
public class MockOsgiFramework
{

    private List<Bundle> bundles;

    private BundleContext bundleContext;

    private List<ExecutionEnvironment> executionEnvironments;

    public MockOsgiFramework()
    {
        executionEnvironments = new ArrayList<ExecutionEnvironment>();
        bundles = new ArrayList<Bundle>();
        bundleContext = mock( BundleContext.class );
        when( bundleContext.getProperty( Constants.FRAMEWORK_EXECUTIONENVIRONMENT ) ).thenAnswer(
            new Answer<String>()
            {
                @Override
                public String answer( final InvocationOnMock invocation )
                    throws Throwable
                {
                    return executionEnvironments();
                }
            }
        );
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
        installBundle().withBundleSymbolicName( "system" );
    }

    private String executionEnvironments()
    {
        if ( executionEnvironments.size() == 0 )
        {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for ( ExecutionEnvironment executionEnvironment : executionEnvironments )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "," );
            }
            sb.append( executionEnvironment.getValue() );
        }
        return sb.toString();
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

    public MockOsgiFramework withExecutionEnvironment( final ExecutionEnvironment... environments )
    {
        executionEnvironments.addAll( asList( environments ) );
        return this;
    }

    public MockBundle getSystemBundle()
    {
        return (MockBundle) getBundleContext().getBundle( 0 );
    }
}
