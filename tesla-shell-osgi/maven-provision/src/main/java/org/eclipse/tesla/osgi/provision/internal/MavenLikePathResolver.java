package org.eclipse.tesla.osgi.provision.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.tesla.osgi.provision.PathResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.spi.locator.ServiceLocator;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class MavenLikePathResolver
    implements PathResolver
{

    private LocalRepositoryManager localRepositoryManager;

    @Inject
    public MavenLikePathResolver( final ServiceLocator serviceLocator )
    {
        final RepositorySystem repositorySystem = serviceLocator.getService( RepositorySystem.class );
        localRepositoryManager = repositorySystem.newLocalRepositoryManager( new LocalRepository( "." ) );
    }

    public String pathFor( final Artifact artifact )
    {
        return localRepositoryManager.getPathForLocalArtifact( artifact );
    }

}
