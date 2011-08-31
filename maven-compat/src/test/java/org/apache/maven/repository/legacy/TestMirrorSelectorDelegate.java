package org.apache.maven.repository.legacy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.repository.MirrorSelectorDelegate;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = MirrorSelectorDelegate.class, hint = TestMirrorSelectorDelegate.HINT )
public class TestMirrorSelectorDelegate
    implements MirrorSelectorDelegate
{

    public static final String HINT = "TestMirrorSelectorDelegate";

    private Map<String, Mirror> mirrors = new HashMap<String, Mirror>();

    public Mirror getMirror( ArtifactRepository repository, List<Mirror> mirrors )
    {
        return this.mirrors.get( getKey( repository.getLayout().getId(), repository.getUrl() ) );
    }

    private String getKey( String layout, String url )
    {
        return layout + ":" + url;
    }

    public void reset()
    {
        mirrors.clear();
    }

    public void setMirror( String layout, String url, String mirrorUrl, String mirrorUsername, String mirrorPassword )
    {
        Mirror mirror = new Mirror();
        mirror.setLayout( layout );
        mirror.setUrl( mirrorUrl );

        if ( mirrorUsername != null )
        {
            Server server = new Server();
            server.setUsername( mirrorUsername );
            server.setPassword( mirrorPassword );

            mirror.setAuthentication( server );
        }

        mirrors.put( getKey( layout, url ), mirror );
    }

}
