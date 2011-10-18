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

import java.util.Arrays;
import java.util.List;

import de.pdark.decentxml.Element;
import de.pdark.decentxml.Node;
import de.pdark.decentxml.Text;

public class PomFormat
{
    /**
     * Project elements in conventional order according to
     * http://maven.apache.org/developers/conventions/code.html#POM_Code_Convention
     */
    List<String> ELEMENTS = Arrays.asList( "modelVersion", //
                                           "parent", //
                                           "groupId", //
                                           "artifactId", //
                                           "version", //
                                           "packaging", //
                                           "name", //
                                           "description", //
                                           "url", //
                                           "inceptionYear", //
                                           "organization", //
                                           "licenses", //
                                           "developers", //
                                           "contributors", //
                                           "mailingLists", //
                                           "prerequisites", //
                                           "modules", //
                                           "scm", //
                                           "issueManagement", //
                                           "ciManagement", //
                                           "distributionManagement", //
                                           "properties", //
                                           "dependencyManagement", //
                                           "dependencies", //
                                           "repositories", //
                                           "pluginRepositories", //
                                           "build", //
                                           "reporting", //
                                           "profiles" //
    );

    /**
     * Adds node to the parent at the right position.
     */
    public void addNode( Element parent, Element element )
    {
        int index = getConventionalNodeIndex( parent, element );

        int depth = getDepth( parent ) + 1;

        parent.addNode( index, newIndentationNode( depth ) );
        parent.addNode( index + 1, element );
    }

    public void indentClosingTag( Element element )
    {
        int depth = getDepth( element );
        element.addNode( newIndentationNode( depth ) );
    }

    private Node newIndentationNode( int depth )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( '\n' );

        for ( int i = 0; i < depth; i++ )
        {
            sb.append( "  " );
        }

        return new Text( sb.toString() );
    }

    private int getDepth( Element parent )
    {
        int d = 0;

        while ( ( parent = parent.getParentElement() ) != null )
        {
            d++;
        }

        return d;
    }

    /**
     * after "previous" and unknown elements and before any text node.
     */
    private int getConventionalNodeIndex( Element parent, Element element )
    {
        int order = ELEMENTS.indexOf( element.getName() );
        if ( order < 0 )
        {
            order = ELEMENTS.size(); // add to the end of the element list
        }

        int index = -1;

        for ( int i = 0; i < parent.getNodes().size(); i++ )
        {
            Node node = parent.getNode( i );

            if ( !( node instanceof Element ) )
            {
                continue;
            }

            int _order = ELEMENTS.indexOf( ( (Element) node ).getName() );

            if ( _order > order )
            {
                break;
            }

            if ( i > index )
            {
                index = i;
            }
        }

        if ( index < 0 )
        {
            // there are no child elements, insert before any text nodes
            return 0;
        }

        return index + 1;
    }
}
