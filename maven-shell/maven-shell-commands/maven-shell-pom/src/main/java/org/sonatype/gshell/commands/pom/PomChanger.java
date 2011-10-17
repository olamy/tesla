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
package org.sonatype.gshell.commands.pom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.codehaus.plexus.util.IOUtil;

import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.XMLParser;
import de.pdark.decentxml.XMLWriter;

public class PomChanger
{
    private PomFormat formatter = new PomFormat();

    private final File pom;

    public PomChanger( File pom )
    {
        this.pom = pom;
    }

    public void perform( AddDependencyOp op )
        throws IOException
    {
        Document document = XMLParser.parse( pom );

        Element project = document.getChild( "project" );

        op.perform( project, formatter );

        String encoding = document.getEncoding();

        XMLWriter w =
            new XMLWriter( encoding != null ? new OutputStreamWriter( new FileOutputStream( pom ), encoding )
                            : new OutputStreamWriter( new FileOutputStream( pom ) ) );

        try
        {
            document.toXML( w );
        }
        finally
        {
            IOUtil.close( w );
        }

    }
}
