package org.apache.maven.repository.legacy;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.AetherMirrorSelector;
import org.apache.maven.repository.MirrorSelector;
import org.apache.maven.repository.MirrorSelectorDelegate;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

/**
 * Tests {@link LegacyRepositorySystem}.
 * 
 * @author Benjamin Bentmann
 */
public class LegacyRepositorySystemTest
    extends PlexusTestCase
{
    private RepositorySystem repositorySystem;

    private TestMirrorSelectorDelegate mirrorSelectorDelegate;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        repositorySystem = lookup( RepositorySystem.class, "default" );
        mirrorSelectorDelegate =
            (TestMirrorSelectorDelegate) lookup( MirrorSelectorDelegate.class, TestMirrorSelectorDelegate.HINT );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        repositorySystem = null;
        mirrorSelectorDelegate.reset();
        mirrorSelectorDelegate = null;
        super.tearDown();
    }

    public void testThatLocalRepositoryWithSpacesIsProperlyHandled()
        throws Exception
    {
        File basedir = new File( "target/spacy path" ).getAbsoluteFile();
        ArtifactRepository repo = repositorySystem.createLocalRepository( basedir );
        assertEquals( basedir, new File( repo.getBasedir() ) );
    }

    public void testAuthenticationHandling()
        throws Exception
    {
        Server server = new Server();
        server.setId( "repository" );
        server.setUsername( "jason" );
        server.setPassword( "abc123" );

        ArtifactRepository repository =
            repositorySystem.createArtifactRepository( "repository", "http://foo", null, null, null );
        repositorySystem.injectAuthentication( Arrays.asList( repository ), Arrays.asList( server ) );
        Authentication authentication = repository.getAuthentication();
        assertNotNull( authentication );
        assertEquals( "jason", authentication.getUsername() );
        assertEquals( "abc123", authentication.getPassword() );
    }

    public void testMirrorSelectionDelegation()
        throws Exception
    {
        ArtifactRepository repository =
            repositorySystem.createArtifactRepository( "repository", "http://foo", null, null, null );

        String mirrorUrl = "http://mirror:8008/mirror";
        String mirrorUsername = "username";
        String mirrorPassword = "password";
        mirrorSelectorDelegate.setMirror( repository.getLayout().getId(), repository.getUrl(), mirrorUrl,
                                          mirrorUsername, mirrorPassword );

        List<ArtifactRepository> repositories = Collections.singletonList( repository );
        repositorySystem.injectMirror( repositories, Collections.<Mirror> emptyList() );
        repositorySystem.injectAuthentication( repositories, Collections.<Server> emptyList() );

        assertEquals( mirrorUrl, repository.getUrl() );
        assertEquals( mirrorUsername, repository.getAuthentication().getUsername() );
        assertEquals( mirrorPassword, repository.getAuthentication().getPassword() );
    }

    public void testMirrorSelectionDelegate_repositorySystemSession()
        throws Exception
    {
        ArtifactRepository repository =
            repositorySystem.createArtifactRepository( "repository", "http://foo", null, null, null );

        String mirrorUrl = "http://mirror:8008/mirror";
        String mirrorUsername = "username";
        String mirrorPassword = "password";
        mirrorSelectorDelegate.setMirror( repository.getLayout().getId(), repository.getUrl(), mirrorUrl,
                                          mirrorUsername, mirrorPassword );

        MirrorSelector mirrorSelector = lookup( MirrorSelector.class );

        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        Map<String, ArtifactRepositoryLayout> layouts = getContainer().lookupMap( ArtifactRepositoryLayout.class );
        session.setMirrorSelector( new AetherMirrorSelector( mirrorSelector, layouts, Collections.<Mirror> emptyList() ) );

        List<ArtifactRepository> repositories = Collections.singletonList( repository );
        repositorySystem.injectMirror( session, repositories );
        repositorySystem.injectAuthentication( session, repositories ); // will use dummy/null auth selector

        assertEquals( mirrorUrl, repository.getUrl() );
        assertEquals( mirrorUsername, repository.getAuthentication().getUsername() );
        assertEquals( mirrorPassword, repository.getAuthentication().getPassword() );

    }
}
