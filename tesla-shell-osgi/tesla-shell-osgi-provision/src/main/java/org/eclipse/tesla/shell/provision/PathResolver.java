package org.eclipse.tesla.shell.provision;

import org.sonatype.aether.artifact.Artifact;

/**
 * TODO
 *
 * @since 1.0
 */
public interface PathResolver
{

    String pathFor( Artifact artifact );

}
