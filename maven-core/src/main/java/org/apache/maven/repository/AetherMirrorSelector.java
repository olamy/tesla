package org.apache.maven.repository;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.Mirror;
import org.sonatype.aether.repository.RemoteRepository;

public class AetherMirrorSelector
    implements org.sonatype.aether.repository.MirrorSelector
{
    private final MirrorSelector mirrorSelector;

    private final List<Mirror> mirrors;

    private final Map<String, ArtifactRepositoryLayout> repositoryLayouts;

    public AetherMirrorSelector( MirrorSelector mirrorSelector,
                                 Map<String, ArtifactRepositoryLayout> repositoryLayouts, List<Mirror> mirrors )
    {
        this.mirrorSelector = mirrorSelector;
        this.mirrors = mirrors;

        // this better be live component map as we don't know what thread will be calling us!
        this.repositoryLayouts = repositoryLayouts;
    }

    public RemoteRepository getMirror( RemoteRepository repository )
    {
        ArtifactRepository artifactRepository = toArtifactRepository( repository );
        if ( artifactRepository == null )
        {
            return null;
        }

        Mirror mirror = mirrorSelector.getMirror( artifactRepository, mirrors );

        if ( mirror == null )
        {
            return null;
        }

        return toRepository( mirror, repository );
    }

    private RemoteRepository toRepository( Mirror mirror, RemoteRepository repository )
    {
        RemoteRepository repo = new RemoteRepository();

        repo.setRepositoryManager( false );
        repo.setId( mirror.getId() );
        repo.setUrl( mirror.getUrl() );

        String type = mirror.getLayout();
        if ( type != null && type.length() > 0 )
        {
            repo.setContentType( type );
        }
        else
        {
            repo.setContentType( repository.getContentType() );
        }

        repo.setPolicy( true, repository.getPolicy( true ) );
        repo.setPolicy( false, repository.getPolicy( false ) );

        repo.setMirroredRepositories( Collections.singletonList( repository ) );

        return repo;
    }

    /**
     * this is specialised adaptor that creates ArtifactRepository only suitable to be passed to maven
     * DefaultMirrorSelector#getMirror method.
     */
    private ArtifactRepository toArtifactRepository( RemoteRepository repo )
    {
        if ( repo == null )
        {
            return null;
        }

        ArtifactRepositoryLayout layout = getLayout( repo.getContentType() );
        if ( layout == null )
        {
            return null;
        }

        return new MavenArtifactRepository( repo.getId(), repo.getUrl(), layout, null, null );
    }

    private ArtifactRepositoryLayout getLayout( String contentType )
    {
        return repositoryLayouts.get( contentType );
    }

}
