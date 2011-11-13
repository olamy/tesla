package org.eclipse.tesla.osgi.provision.internal;

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
import org.apache.felix.bundlerepository.impl.Referral;
import org.apache.felix.bundlerepository.impl.RepositoryImpl;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.eclipse.tesla.osgi.provision.Provisioner;
import org.eclipse.tesla.osgi.provision.url.masor.MavenArtifactSetObrRepository;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.inject.EagerSingleton;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@EagerSingleton
class DefaultProvisioner
    implements Provisioner
{

    private final Logger logger;

    private final MavenArtifactSetObrRepository mavenObrArtifactSet;

    private final RepositoryAdmin repositoryAdmin;

    private final BundleContext bundleContext;

    @Inject
    DefaultProvisioner( final MavenArtifactSetObrRepository mavenObrArtifactSet,
                        final RepositoryAdmin repositoryAdmin,
                        final BundleContext bundleContext )
    {
        logger = LoggerFactory.getLogger( this.getClass() );

        this.mavenObrArtifactSet = mavenObrArtifactSet;
        this.repositoryAdmin = repositoryAdmin;
        this.bundleContext = bundleContext;

        bindToProvisioningRequests();
    }

    private void bindToProvisioningRequests()
    {
        try
        {
            new ServiceTracker(
                bundleContext,
                bundleContext.createFilter(
                    String.format( "(&(objectClass=%s)(target=maven-provisioning))", String.class.getName() )
                ),
                new ServiceTrackerCustomizer()
                {

                    @Override
                    public Object addingService( final ServiceReference serviceReference )
                    {
                        List<String> coordinates = new ArrayList<String>();
                        final String[] propertyKeys = serviceReference.getPropertyKeys();
                        if ( propertyKeys != null )
                        {
                            for ( final String propertyKey : propertyKeys )
                            {
                                if ( propertyKey.startsWith( "coordinates" ) )
                                {
                                    coordinates.add( (String) serviceReference.getProperty( propertyKey ) );
                                }
                            }
                        }
                        if ( !coordinates.isEmpty() )
                        {
                            final ProvisionSet provisionSet = resolve(
                                coordinates.toArray( new String[coordinates.size()] )
                            );
                            provisionSet.installAndStart();
                        }
                        return bundleContext.getService( serviceReference );
                    }

                    @Override
                    public void modifiedService( final ServiceReference serviceReference, final Object o )
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void removedService( final ServiceReference serviceReference, final Object o )
                    {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                } ).open();
            // TODO how do we close the above tracker?
        }
        catch ( InvalidSyntaxException e )
        {
            // should never happen
            throw new RuntimeException( e );
        }

    }

    public ProvisionSet resolve( final String... coordinates )
    {
        logger.info( "Resolving {}", Arrays.toString( coordinates ) );
        final String url = mavenObrArtifactSet.create( coordinates );
        logger.debug( "Using OBR to provision from {}", url );

        try
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "State before resolving: " );
                for ( final Bundle bundle : bundleContext.getBundles() )
                {
                    logger.debug( "  - {}", bundle );
                }
            }
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
            if ( !resolved )
            {
                logProblems( resolver );
            }
            return new DefaultProvisionSet( coordinates, bundleContext, resolver, resolved );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void logProblems( final Resolver resolver )
    {
        if ( logger.isErrorEnabled() )
        {
            final Reason[] reasons = resolver.getUnsatisfiedRequirements();
            logger.error( "Found the following problems: " );
            for ( final Reason reason : reasons )
            {
                if ( reason.getResource() == null || reason.getResource().getId() == null )
                {
                    logger.error( "  {}", reason.getRequirement() );
                }
                else
                {
                    logger.error( "  {} -> {}", reason.getResource(), reason.getRequirement() );
                }
            }
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
