package org.eclipse.tesla.shell.provision.internal;

import static java.util.Arrays.asList;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.Reason;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Resolver;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.Referral;
import org.apache.felix.bundlerepository.impl.RepositoryImpl;
import org.eclipse.tesla.shell.provision.Provisioner;
import org.eclipse.tesla.shell.provision.url.masor.MavenArtifactSetObrRepository;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
class DefaultProvisioner
    implements Provisioner
{

    private MavenArtifactSetObrRepository mavenObrArtifactSet;

    private RepositoryAdmin repositoryAdmin;

    private BundleContext bundleContext;

    @Inject
    DefaultProvisioner( final MavenArtifactSetObrRepository mavenObrArtifactSet,
                        final RepositoryAdmin repositoryAdmin,
                        final BundleContext bundleContext )
    {
        this.mavenObrArtifactSet = mavenObrArtifactSet;
        this.repositoryAdmin = repositoryAdmin;
        this.bundleContext = bundleContext;
    }

    public Bundle[] install( final String... coordinates )
    {
        return provision( coordinates );
    }

    public Bundle[] installAndStart( final String... coordinates )
    {
        final Bundle[] bundles = provision( coordinates );
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
                    bundle.start();
                }
                catch ( BundleException e )
                {
                    System.out.println( e.getMessage() );
                }
            }
        }
        return bundles;
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

    private Bundle[] provision( final String... coordinates )
    {
        final String url = mavenObrArtifactSet.create( coordinates );
        try
        {
            final List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repositoryAdmin.getSystemRepository() );
            repositories.add( repositoryAdmin.getLocalRepository() );
            final List<Repository> setRelatedRepositories = repositories( url );
            repositories.addAll( setRelatedRepositories );

            final Resolver resolver = repositoryAdmin.resolver(
                repositories.toArray( new Repository[repositories.size()] )
            );
            for ( final String coordinate : coordinates )
            {
                resolver.add(
                    repositoryAdmin.getHelper().requirement(
                        "maven",
                        String.format( "(maven-coordinates=%s)", coordinate )
                    )
                );
            }
            boolean resolved = resolver.resolve();
            if ( resolved )
            {
                resolver.deploy( 0 );
                return getInstalledBundles( resolver );
            }
            else
            {
                printProblems( resolver );
                return null;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
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

    private void printProblems( final Resolver resolver )
    {
        final Reason[] reasons = resolver.getUnsatisfiedRequirements();
        for ( final Reason reason : reasons )
        {
            System.out.println( String.format( "%s->%s", reason.getResource(), reason.getRequirement() ) );
        }
    }

    private List<Repository> repositories( final String url )
        throws Exception
    {
        final List<Repository> repositories = new ArrayList<Repository>();
        final RepositoryImpl repository = (RepositoryImpl) repositoryAdmin.getHelper().repository( new URL( url ) );
        repositories.add( repository );
        final Referral[] referrals = repository.getReferrals();
        if ( referrals != null )
        {
            for ( Referral referral : referrals )
            {
                repositories.addAll( repositories( referral.getUrl() ) );
            }
        }
        return repositories;
    }

    public void dryRun( final String... coordinates )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
