/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.tesla.shell.preparator.validation;

import org.apache.karaf.shell.commands.CommandException;
import org.eclipse.tesla.shell.preparator.ArgumentDescriptor;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * Exception thrown in case that an action implementation has more arguments with same index.
 */
public class MultipleArgumentsWithSameIndexException
    extends CommandException
{

    public MultipleArgumentsWithSameIndexException( final CommandDescriptor command,
                                                    final ArgumentDescriptor arg1,
                                                    final ArgumentDescriptor arg2 )
    {
        super(
            Ansi.ansi()
                .fg( Ansi.Color.RED )
                .a( "Error executing command " )
                .a( command.getScope() )
                .a( ":" )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( command.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( ": arguments " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg1.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " and " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( arg2.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " have the same index: " )
                .a( arg1.getIndex() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format(
                "Arguments %s and %s have same the same index: %s",
                arg1.getName(), arg2.getName(), arg1.getIndex()
            )
        );
    }

}
