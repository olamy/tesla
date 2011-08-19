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
