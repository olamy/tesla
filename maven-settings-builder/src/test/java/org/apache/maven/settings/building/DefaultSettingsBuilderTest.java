package org.apache.maven.settings.building;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.SettingsWriter;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.annotations.Requirement;

public class DefaultSettingsBuilderTest
    extends PlexusTestCase
{
    @Requirement
    private SettingsBuilder settingsBuilder;

    @Requirement
    private SettingsWriter settingsWriter;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        settingsBuilder = lookup( SettingsBuilder.class );
        settingsWriter = lookup( SettingsWriter.class );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        settingsBuilder = null;
        settingsWriter = null;

        super.tearDown();
    }

    public void testCustomSettingsSource()
        throws Exception
    {
        SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();

        settingsRequest.setUserSettingsSource( newSettingsSource( "userSettings" ) );
        settingsRequest.setGlobalSettingsSource( newSettingsSource( "globalSettings" ) );

        settingsRequest.addCustomSettingsSource( newSettingsSource( "customSettings" ) );

        Settings settings = settingsBuilder.build( settingsRequest ).getEffectiveSettings();

        assertEquals( 1, settings.getServers().size() );
        assertEquals( "customSettings", settings.getServers().get( 0 ).getUsername() );
    }

    private SettingsSource newSettingsSource( final String string )
        throws IOException
    {
        Settings settings = new Settings();

        Server server = new Server();

        server.setId( "id" );
        server.setUsername( string );

        settings.addServer( server );

        final ByteArrayOutputStream buf = new ByteArrayOutputStream();

        settingsWriter.write( buf, null, settings );

        return new SettingsSource()
        {

            public String getLocation()
            {
                return string;
            }

            public InputStream getInputStream()
                throws IOException
            {
                return new ByteArrayInputStream( buf.toByteArray() );
            }
        };
    }

}
