/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.sonatype.gshell.commands.pom.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.sonatype.gshell.commands.pom.AddDependencyOp;
import org.sonatype.gshell.commands.pom.PomChanger;

public class AddDependencyOnTest
{

    @Test
    public void nodependencies()
        throws Exception
    {
        doTest( "nodependencies" );
    }

    @Test
    public void dependencies()
        throws Exception
    {
        doTest( "dependencies" );
    }

    private void doTest( String name )
        throws IOException
    {
        File pom = new File( getBasedir( name ), "pom.xml" );

        new PomChanger( pom ).perform( new AddDependencyOp( "new-groupId", "new-artifactId", "new-version" ) );

        assertFileContent( pom );
    }

    private void assertFileContent( File actual )
        throws IOException
    {
        File expected = new File( actual.getParentFile(), actual.getName() + "_expected" );
        Assert.assertEquals( toAsciiString( expected ), toAsciiString( actual ) );
    }

    private String toAsciiString( File file )
        throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
        try
        {
            String str;
            while ( ( str = r.readLine() ) != null )
            {
                sb.append( str ).append( '\n' );
            }
        }
        finally
        {
            r.close();
        }
        return sb.toString();
    }

    public static File getBasedir( String name )
        throws IOException
    {
        File src = new File( "src/test/resources/", name );
        File dst = new File( "target/", name );

        if ( dst.isDirectory() )
        {
            FileUtils.deleteDirectory( dst );
        }
        else if ( dst.isFile() )
        {
            if ( !dst.delete() )
            {
                throw new IOException( "Can't delete file " + dst.toString() );
            }
        }

        FileUtils.copyDirectoryStructure( src, dst );

        return dst;
    }

}
