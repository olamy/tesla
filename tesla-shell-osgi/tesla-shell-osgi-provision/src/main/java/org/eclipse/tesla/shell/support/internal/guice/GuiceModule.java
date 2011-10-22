package org.eclipse.tesla.shell.support.internal.guice;

import javax.inject.Named;

import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
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
    }

}
