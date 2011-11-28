/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.command.standard;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.List;

import org.apache.karaf.shell.console.Completer;
import jline.console.completer.FileNameCompleter;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class CoordinatesCompleter
    implements Completer
{

    public static final CoordinatesCompleter INSTANCE = new CoordinatesCompleter();

    private final FileNameCompleter delegate;

    private CoordinatesCompleter()
    {
        this.delegate = new FileNameCompleter();
    }

    @Override
    public int complete( final String buffer, final int cursor, final List<String> candidates )
    {
        String[] segments = ( buffer == null ? "" : buffer ).split( ":" );
        if ( segments.length > 3 )
        {
            return cursor;
        }

        // TODO get local repository from settings

        File localRepository = new File( System.getProperty( "user.home" ), ".m2/repository" ).getAbsoluteFile();
        if ( !localRepository.getPath().endsWith( File.separator ) )
        {
            localRepository = new File( localRepository, File.separator );
        }

        String workBuffer = buffer == null ? "" : buffer;
        if ( segments.length > 0 && segments[0].trim().length() > 0 )
        {
            workBuffer = segments[0].replace( '.', File.separatorChar ) + workBuffer.substring( segments[0].length() );
        }
        workBuffer = workBuffer.replace( ':', File.separatorChar );
        workBuffer = localRepository + workBuffer;

        if ( segments.length > 2 && new File( workBuffer ).exists() )
        {
            return cursor;
        }

        int index = delegate.complete(
            workBuffer, cursor + localRepository.getAbsolutePath().length(), (List) candidates
        );

        final Iterator<String> it = candidates.iterator();
        while ( it.hasNext() )
        {
            final String candidate = it.next().trim();
            if ( ( candidate.startsWith( "maven-metadata" ) && candidate.endsWith( ".xml" ) )
                || candidate.startsWith( "." ) )
            {
                it.remove();
            }
        }

        if ( candidates.size() == 1 && candidates.get( 0 ).endsWith( File.separator ) )
        {
            String candidate = candidates.remove( 0 );
            candidate = candidate.substring( 0, candidate.length() - 1 );

            File dir = new File( new File( workBuffer.substring( 0, index ) ), candidate );

            if ( depthUnderIsBiggerThen( dir, 0, 2 ) )
            {
                // we have a group
                candidates.add( candidate + "." );
            }
            else
            {
                workBuffer = ( buffer == null ? "" : buffer );
                workBuffer = workBuffer.substring( 0, index - localRepository.getAbsolutePath().length() );
                workBuffer += candidate;
                segments = workBuffer.split( ":" );
                if ( segments.length > 2 )
                {
                    candidates.add( candidate );
                }
                else
                {
                    candidates.add( candidate + ":" );
                }
            }
        }

        return index - localRepository.getAbsolutePath().length();
    }

    private boolean depthUnderIsBiggerThen( final File file, int startDepth, int depth )
    {
        if ( startDepth > depth )
        {
            return true;
        }
        if ( file.isFile() )
        {
            return false;
        }
        final File[] dirs = file.listFiles( new FileFilter()
        {
            @Override
            public boolean accept( final File file )
            {
                return file.isDirectory();
            }
        } );
        if ( dirs != null )
        {
            for ( final File dir : dirs )
            {
                final boolean biggerInSubDir = depthUnderIsBiggerThen( dir, startDepth + 1, depth );
                if ( biggerInSubDir )
                {
                    return true;
                }
            }
        }
        return false;
    }

}
