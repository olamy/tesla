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

import java.lang.reflect.Type;

import org.apache.karaf.shell.commands.CommandException;
import org.apache.karaf.shell.commands.converter.GenericType;
import org.eclipse.tesla.shell.preparator.CommandDescriptor;
import org.eclipse.tesla.shell.preparator.OptionDescriptor;
import org.fusesource.jansi.Ansi;

/**
 * TODO
 *
 * @since 1.0
 */
public class OptionConversionException
    extends CommandException
{

    public OptionConversionException( final CommandDescriptor command,
                                      final OptionDescriptor option,
                                      final Object value,
                                      final Type expectedType,
                                      final Exception cause )
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
                .a( ": unable to convert option " )
                .a( Ansi.Attribute.INTENSITY_BOLD )
                .a( option.getName() )
                .a( Ansi.Attribute.INTENSITY_BOLD_OFF )
                .a( " with value '" )
                .a( value )
                .a( "' to type " )
                .a( new GenericType( expectedType ).toString() )
                .fg( Ansi.Color.DEFAULT )
                .toString(),
            String.format(
                "Unable to convert option '%s' with value '%s' to type %s",
                option.getName(), value, new GenericType( expectedType ).toString()
            ),
            cause

        );
    }

}
