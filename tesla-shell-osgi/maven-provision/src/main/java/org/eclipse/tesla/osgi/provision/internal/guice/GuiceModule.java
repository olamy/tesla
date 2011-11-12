package org.eclipse.tesla.osgi.provision.internal.guice;

import javax.inject.Named;

import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.dependency.RemoteMavenDependencyTreeResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.model.RemoteMavenModelResolverUsingSettings;
import com.google.inject.AbstractModule;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class GuiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( MavenArtifactResolver.class ).to( RemoteMavenArtifactResolverUsingSettings.class );
        bind( MavenModelResolver.class ).to( RemoteMavenModelResolverUsingSettings.class );
        bind( MavenDependencyTreeResolver.class ).to( RemoteMavenDependencyTreeResolverUsingSettings.class );
    }

}
