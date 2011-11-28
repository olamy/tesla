/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.masor.internal;

import java.security.MessageDigest;
import javax.inject.Named;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
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
