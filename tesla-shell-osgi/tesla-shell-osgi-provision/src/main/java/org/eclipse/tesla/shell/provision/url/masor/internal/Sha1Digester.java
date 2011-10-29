package org.eclipse.tesla.shell.provision.url.masor.internal;

import java.security.MessageDigest;
import javax.inject.Named;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class Sha1Digester
    implements Digester
{

    @Override
    public String digest( final String... values )
    {
        try
        {
            final MessageDigest md = MessageDigest.getInstance( "SHA-1" );
            for ( String value : values )
            {
                md.update( value.getBytes( "utf-8" ), 0, value.length() );
            }
            byte[] sha1hash = md.digest();
            return convertToHex( sha1hash );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Could not calculate digest", e );
        }
    }

    private static String convertToHex( byte[] data )
    {
        final StringBuilder buf = new StringBuilder();
        for ( final byte entry : data )
        {
            int halfByte = ( entry >>> 4 ) & 0x0F;
            int oneByte = 0;
            do
            {
                if ( ( 0 <= halfByte ) && ( halfByte <= 9 ) )
                {
                    buf.append( (char) ( '0' + halfByte ) );
                }
                else
                {
                    buf.append( (char) ( 'a' + ( halfByte - 10 ) ) );
                }
                halfByte = entry & 0x0F;
            }
            while ( oneByte++ < 1 );
        }
        return buf.toString();
    }

}
