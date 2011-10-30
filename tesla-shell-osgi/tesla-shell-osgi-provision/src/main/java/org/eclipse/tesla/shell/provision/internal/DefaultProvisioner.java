package org.eclipse.tesla.shell.provision.internal;

import static java.util.Arrays.asList;

import java.net.URL;
import java.util.ArrayList;
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

    @Inject
    DefaultProvisioner( final MavenArtifactSetObrRepository mavenObrArtifactSet,
                        final RepositoryAdmin repositoryAdmin )
    {
        this.mavenObrArtifactSet = mavenObrArtifactSet;
        this.repositoryAdmin = repositoryAdmin;
    }

    public void provision( final String... coordinates )
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
            }
            else
            {
                printProblems( resolver );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void printProblems( final Resolver resolver )
    {
        final Reason[] reasons = resolver.getUnsatisfiedRequirements();
        for ( final Reason reason : reasons )
        {
            System.out.println(String.format( "%s->%s", reason.getResource(), reason.getRequirement() ));
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
