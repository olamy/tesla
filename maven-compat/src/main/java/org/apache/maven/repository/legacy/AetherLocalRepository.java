package org.apache.maven.repository.legacy;

/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.io.File;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.sonatype.aether.metadata.Metadata;
import org.sonatype.aether.repository.LocalArtifactRequest;
import org.sonatype.aether.repository.LocalArtifactResult;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.metadata.DefaultMetadata;

/**
 * 
 */
@SuppressWarnings( "deprecation" )
class AetherLocalRepository
    extends MavenArtifactRepository
{

    private final LocalRepositoryManager delegate;

    private final LegacySupport legacySupport;

    public AetherLocalRepository( LocalRepositoryManager delegate, ArtifactRepositoryLayout layout,
                                  LegacySupport legacySupport )
    {
        super( RepositorySystem.DEFAULT_LOCAL_REPO_ID, "file://"
            + delegate.getRepository().getBasedir().toURI().getRawPath(), layout,
               new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                             ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
               new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                             ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ) );
        this.delegate = delegate;
        this.legacySupport = legacySupport;
    }

    @Override
    public String pathOf( Artifact artifact )
    {
        LocalArtifactRequest request = new LocalArtifactRequest();
        request.setArtifact( RepositoryUtils.toArtifact( artifact ) );

        MavenSession mvnSession = legacySupport.getSession();
        if ( mvnSession != null )
        {
            MavenProject mvnProject = mvnSession.getCurrentProject();
            if ( mvnProject != null )
            {
                request.setRepositories( mvnProject.getRemoteProjectRepositories() );
            }
            LocalArtifactResult result = delegate.find( legacySupport.getRepositorySession(), request );
            File file = result.getFile();
            if ( file != null )
            {
                String path = file.getAbsolutePath();
                path = path.substring( delegate.getRepository().getBasedir().getAbsolutePath().length() + 1 );
                return path.replace( '\\', '/' );
            }
        }

        return delegate.getPathForLocalArtifact( request.getArtifact() );
    }

    @Override
    public String pathOfLocalRepositoryMetadata( ArtifactMetadata metadata, ArtifactRepository repository )
    {
        DefaultMetadata md =
            new DefaultMetadata( metadata.getGroupId(), metadata.getArtifactId(), metadata.getBaseVersion(),
                                 metadata.getRemoteFilename(), Metadata.Nature.RELEASE_OR_SNAPSHOT );
        RemoteRepository repo = RepositoryUtils.toRepo( repository );
        return delegate.getPathForRemoteMetadata( md, repo, "" );
    }

}
