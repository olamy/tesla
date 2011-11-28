/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
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
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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
