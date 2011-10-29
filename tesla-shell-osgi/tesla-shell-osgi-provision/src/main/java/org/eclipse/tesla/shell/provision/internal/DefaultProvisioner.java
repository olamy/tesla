package org.eclipse.tesla.shell.provision.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;
import org.apache.felix.bundlerepository.Resolver;
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
        final String setUrl = mavenObrArtifactSet.create( coordinates );
        try
        {
            final List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repositoryAdmin.getSystemRepository() );
            repositories.add( repositoryAdmin.getLocalRepository() );
            repositories.addAll( repositories( setUrl ) );

            final Resolver resolver = repositoryAdmin.resolver(
                repositories.toArray( new Repository[repositories.size()] )
            );
            for ( final String coordinate : coordinates )
            {
                resolver.add(
                    repositoryAdmin.getHelper().requirement(
                        coordinate,
                        String.format( "(maven-coordinates=%s)", coordinate )
                    )
                );
            }
            boolean resolved = resolver.resolve();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
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
