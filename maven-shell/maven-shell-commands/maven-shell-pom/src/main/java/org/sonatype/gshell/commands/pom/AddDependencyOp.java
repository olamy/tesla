package org.sonatype.gshell.commands.pom;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
