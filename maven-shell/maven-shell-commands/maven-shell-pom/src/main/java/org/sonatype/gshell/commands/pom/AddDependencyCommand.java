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

import java.io.File;

import javax.inject.Inject;

import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.support.CommandActionSupport;
import org.sonatype.gshell.file.FileSystemAccess;
import org.sonatype.gshell.util.FileAssert;
import org.sonatype.gshell.util.cli2.Argument;

@Command( name = "pom/add-dependency" )
public class AddDependencyCommand
    extends CommandActionSupport
{
    @Argument( required = true )
    private String gav;

    private final FileSystemAccess fileSystem;

    @Inject
    public AddDependencyCommand( FileSystemAccess fileSystem )
    {
        this.fileSystem = fileSystem;
    }

    public Object execute( CommandContext context )
        throws Exception
    {
        File pom = new File( fileSystem.getUserDir(), "pom.xml" );

        new FileAssert( pom ).exists().isFile();

        // TODO maven indexer integration

        DefaultArtifact gav = new DefaultArtifact( this.gav );

        new PomChanger( pom ).perform( new AddDependencyOp( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() ) );

        // TODO UNDO support

        return Result.SUCCESS;
    }
}
