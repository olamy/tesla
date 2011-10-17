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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sonatype.gshell.commands.pom.PomFormat;

import de.pdark.decentxml.Element;
import de.pdark.decentxml.Text;

public class PomFormatTest
{
    @Test
    public void addNode()
    {
        PomFormat f = new PomFormat();

        Element project = new Element( "project" );

        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "parent" ) );
        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "reporting" ) );
        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "dependencies" ) );
        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "dependencies" ) );
        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "modelVersion" ) );
        project.addNode( new Text( "   " ) );
        f.addNode( project, new Element( "profiles" ) );

        List<Element> nodes = project.getChildren();

        Assert.assertEquals( "modelVersion", nodes.get( 0 ).getName() );
        Assert.assertEquals( "parent", nodes.get( 1 ).getName() );
        Assert.assertEquals( "dependencies", nodes.get( 2 ).getName() );
        Assert.assertEquals( "dependencies", nodes.get( 3 ).getName() );
        Assert.assertEquals( "reporting", nodes.get( 4 ).getName() );
        Assert.assertEquals( "profiles", nodes.get( 5 ).getName() );
    }
}
