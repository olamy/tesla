/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.command.standard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.Completer;
import org.eclipse.tesla.osgi.provision.ProvisionSet;
import org.eclipse.tesla.osgi.provision.Provisioner;
import org.eclipse.tesla.shell.Completable;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
@Command( scope = "standard", name = "provision", description = "Provision jars" )
class ProvisionCommand
    implements Action, Completable
{

    @Argument( name = "coordinates", description = "Maven coordinates of jar to be provisioned", required = true,
               multiValued = true )
    private String[] coordinates;

    @Option( name = "-d", aliases = { "--dryRun" },
             description = "Do not actually install just explain what will be done" )
    private boolean dryRun;

    private final Provisioner provisioner;

    @Inject
    ProvisionCommand( final Provisioner provisioner )
    {
        this.provisioner = provisioner;
    }

    @Override
    public Object execute( final CommandSession commandSession )
        throws Exception
    {
        final ProvisionSet provisionSet = provisioner.resolve( coordinates );
        if ( provisionSet.hasProblems() )
        {
            provisionSet.printProblems( System.err );
        }
        else
        {
            provisionSet.installAndStart();
        }
        return null;
    }

    @Override
    public List<? extends Completer> getCompleters()
    {
        return Arrays.asList( CoordinatesCompleter.INSTANCE );
    }

    @Override
    public Map<String, ? extends Completer> getOptionalCompleters()
    {
        return Collections.emptyMap();
    }

}
