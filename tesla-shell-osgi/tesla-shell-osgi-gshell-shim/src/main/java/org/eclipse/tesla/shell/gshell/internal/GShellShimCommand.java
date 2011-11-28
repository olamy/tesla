/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.jline.CommandSessionHolder;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.ShellHolder;
import org.sonatype.gshell.util.NameAware;
import org.sonatype.inject.BeanEntry;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
class GShellShimCommand
    extends AbstractCommand
    implements CompletableFunction
{

    private BeanEntry<Annotation, Object> beanEntry;

    GShellShimCommand( final BeanEntry<Annotation, Object> beanEntry )
    {
        this.beanEntry = beanEntry;
    }

    @Override
    public Class<? extends Action> getActionClass()
    {
        return CommandActionProxy.class;
    }

    @Override
    public Action createNewAction()
    {
        final CommandAction commandAction = (CommandAction) beanEntry.getProvider().get();
        if ( commandAction instanceof NameAware )
        {
            String name = beanEntry.getImplementationClass().getAnnotation( Command.class ).name();
            if ( name == null )
            {
                name = commandAction.getClass().getSimpleName();
            }
            try
            {
                ( (NameAware) commandAction ).setName( name );
            }
            catch ( Exception ignore )
            {
                // ignore
            }
        }
        return new CommandActionProxy( commandAction );
    }

    @Override
    protected ActionPreparator getPreparator()
        throws Exception
    {
        return new GShellShimActionPreparator();
    }

    @Override
    public List<Completer> getCompleters()
    {
        final CommandAction commandAction = (CommandAction) beanEntry.getProvider().get();
        final jline.console.completer.Completer[] completers = commandAction.getCompleters();
        if ( completers != null && completers.length > 0 )
        {
            final ArrayList<Completer> shimCompleters = new ArrayList<Completer>();
            for ( final jline.console.completer.Completer completer : completers )
            {
                shimCompleters.add( new Completer()
                {
                    @SuppressWarnings( "unchecked" )
                    @Override
                    public int complete( final String buffer, final int cursor, final List<String> candidates )
                    {
                        final CommandSession session = CommandSessionHolder.getSession();
                        ShellHolder.set( new GShellShimShell( session ) );
                        return completer.complete( buffer, cursor, (List) candidates );
                    }
                } );
            }
            return shimCompleters;
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Completer> getOptionalCompleters()
    {
        return Collections.emptyMap();
    }

}
