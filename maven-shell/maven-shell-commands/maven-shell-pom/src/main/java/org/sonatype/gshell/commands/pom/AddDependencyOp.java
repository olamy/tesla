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

import de.pdark.decentxml.Element;

public class AddDependencyOp
{
    private final String groupId;

    private final String artifactId;

    private final String version;

    public AddDependencyOp( String groupId, String artifactId, String version )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public void perform( Element container, PomFormat formatter )
    {
        Element dependencies = container.getChild( "dependencies" );

        if ( dependencies == null )
        {
            dependencies = new Element( "dependencies" );

            formatter.addNode( container, dependencies );
            formatter.indentClosingTag( dependencies );
        }

        // TODO validate dependency is not there already
        // do we need to consider profiles? what about inactive profiles?

        Element dependency = new Element( "dependency" );

        // clumsy -- need to add node to the parent before adding any children to get proper indentation.
        formatter.addNode( dependencies, dependency );

        // technically, groupdId is not required, but I don't trust this "feature"
        Element groupId = new Element( "groupId" );
        groupId.setText( this.groupId );
        formatter.addNode( dependency, groupId );

        // this is the only required subelement, everything else can come from dependencyManagement
        Element artifactId = new Element( "artifactId" );
        artifactId.setText( this.artifactId );
        formatter.addNode( dependency, artifactId );

        if ( this.version != null )
        {
            Element version = new Element( "version" );
            version.setText( this.version );
            formatter.addNode( dependency, version );
        }

        formatter.indentClosingTag( dependency );
    }
}
