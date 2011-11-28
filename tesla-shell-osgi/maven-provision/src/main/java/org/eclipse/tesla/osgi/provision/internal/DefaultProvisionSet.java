/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.internal;

import static java.util.Arrays.asList;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.bundlerepository.Reason;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
class DefaultProvisionSet
    implements ProvisionSet
{

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    private final String[] coordinates;

    private final Resolver resolver;

    private final boolean resolved;

    private final BundleContext bundleContext;

    public DefaultProvisionSet( final String[] coordinates,
                                final BundleContext bundleContext,
                                final Resolver resolver,
                                final boolean resolved )
    {
        this.coordinates = coordinates;
        this.bundleContext = bundleContext;
        this.resolver = resolver;
        this.resolved = resolved;
    }

    @Override
    public Bundle[] install()
    {
        resolver.deploy( 0 );
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "State after provisioning: " );
            for ( final Bundle bundle : bundleContext.getBundles() )
            {
                logger.debug( "  - {}", bundle );
            }
        }
        return getInstalledBundles( resolver );

    }

    @Override
    public Bundle[] installAndStart()
    {
        logger.info( "Installing {}", Arrays.toString( coordinates ) );
        final Bundle[] bundles = install();
        if ( bundles.length > 0 )
        {
            final List<Bundle> fragments = getFragments( bundles );
            refreshHosts( fragments );
            final List<Bundle> toBeStarted = new ArrayList<Bundle>( Arrays.asList( bundles ) );
            toBeStarted.removeAll( fragments );
            for ( final Bundle bundle : toBeStarted )
            {
                try
                {
                    logger.info( "Starting {}", bundle );
                    bundle.start();
                }
                catch ( BundleException e )
                {
                    logger.warn( "Cannot start bundle " + bundle, e );
                }
            }
        }
        return bundles;
    }

    @Override
    public boolean hasProblems()
    {
        return !resolved;
    }

    @Override
    public void printProblems( final PrintStream err )
    {
        final Reason[] reasons = resolver.getUnsatisfiedRequirements();
        err.println( "Found the following problems: " );
        for ( final Reason reason : reasons )
        {
            // TODO make a human readable printout
            if ( reason.getResource() == null || reason.getResource().getId() == null )
            {
                err.println( String.format( "  %s", reason.getRequirement() ) );
            }
            else
            {
                err.println( String.format( "  %s -> %s", reason.getResource(), reason.getRequirement() ) );
            }
        }
    }

    private Bundle[] getInstalledBundles( final Resolver resolver )
    {
        final List<Bundle> installed = new ArrayList<Bundle>();
        final Resource[] addedResources = resolver.getRequiredResources();
        // TODO shall we also add the optional resources
        if ( addedResources != null )
        {
            final PackageAdmin packageAdmin = getPackageAdmin();
            if ( packageAdmin != null )
            {
                for ( final Resource resource : addedResources )
                {
                    final String symbolicName = resource.getSymbolicName();
                    final Version version = resource.getVersion();
                    final Bundle[] bundles = packageAdmin.getBundles( symbolicName, version.toString() );
                    if ( bundles != null )
                    {
                        installed.addAll( asList( bundles ) );
                    }
                }
            }
        }
        return installed.toArray( new Bundle[installed.size()] );
    }

    private PackageAdmin getPackageAdmin()
    {
        final ServiceReference ref = bundleContext.getServiceReference( PackageAdmin.class.getName() );
        return (PackageAdmin) bundleContext.getService( ref );
    }

    private List<Bundle> getFragments( final Bundle[] bundles )
    {
        final List<Bundle> fragments = new ArrayList<Bundle>();
        for ( final Bundle bundle : bundles )
        {
            if ( bundle.getHeaders().get( Constants.FRAGMENT_HOST ) != null )
            {
                fragments.add( bundle );
            }
        }
        return fragments;
    }

    private void refreshHosts( final List<Bundle> fragments )
    {
        final PackageAdmin packageAdmin = getPackageAdmin();
        for ( final Bundle fragment : fragments )
        {
            final String bsn = getHostSymbolicName( fragment );
            final String versionRange = getHostVersionRange( fragment );
            final Bundle[] hosts = packageAdmin.getBundles( bsn, versionRange );
            if ( hosts != null )
            {
                packageAdmin.refreshPackages( hosts );
            }
        }
    }

    private String getHostVersionRange( final Bundle fragment )
    {
        // TODO look how they do it in Felix
        final String fragmentHost = (String) fragment.getHeaders().get( Constants.FRAGMENT_HOST );
        final String[] segments = fragmentHost.split( ";" );
        String versionRange = null;
        for ( final String segment : segments )
        {
            if ( segment.trim().startsWith( "bundle-version=" ) )
            {
                versionRange = segment.trim()
                    .replaceFirst( "bundle-version=", "" )
                    .replace( "\"", "" );
            }
        }
        return versionRange;
    }

    private String getHostSymbolicName( final Bundle fragment )
    {
        final String fragmentHost = (String) fragment.getHeaders().get( Constants.FRAGMENT_HOST );
        return fragmentHost.split( ";" )[0];
    }

}
