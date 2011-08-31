package org.apache.maven.repository;

import java.util.Collections;
import java.util.Map;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.Mirror;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.repository.RemoteRepository;

public class AetherMirrorSelectorTest
    extends PlexusTestCase
{

    private MirrorSelector mirrorSelector;

    private Map<String, ArtifactRepositoryLayout> repositoryLayouts;

    private TestMirrorSelectorDelegate delegate;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        this.mirrorSelector = lookup( MirrorSelector.class );
        this.repositoryLayouts = getContainer().lookupMap( ArtifactRepositoryLayout.class );
        this.delegate =
            (TestMirrorSelectorDelegate) lookup( MirrorSelectorDelegate.class, TestMirrorSelectorDelegate.HINT );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        mirrorSelector = null;
        repositoryLayouts = null;

        delegate.reset();
        delegate = null;
        super.tearDown();
    }

    public void testGetMirrorFromDelegate()
    {
        AetherMirrorSelector selector =
            new AetherMirrorSelector( mirrorSelector, repositoryLayouts, Collections.<Mirror> emptyList() );

        RemoteRepository repository = new RemoteRepository( "id", "default", "http://test:8080/test" );

        String mirrorUrl = "http://mirror:8008/mirror";
        String mirrorUsername = "username";
        String mirrorPassword = "password";
        delegate.setMirror( repository.getContentType(), repository.getUrl(), mirrorUrl, mirrorUsername, mirrorPassword );

        RemoteRepository mirror = selector.getMirror( repository );

        assertEquals( mirrorUrl, mirror.getUrl() );
        assertEquals( mirrorUsername, mirror.getAuthentication().getUsername() );
        assertEquals( mirrorPassword, mirror.getAuthentication().getPassword() );
    }
}
