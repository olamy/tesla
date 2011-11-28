/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.eclipse.tesla.shell.Completable;
import org.eclipse.tesla.shell.support.GuiceCommandSupport;
import org.sonatype.inject.BeanEntry;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class ActionProxy
    extends GuiceCommandSupport
    implements CompletableFunction
{

    private BeanEntry<Annotation, Object> beanEntry;

    ActionProxy( final BeanEntry<Annotation, Object> beanEntry )
    {
        this.beanEntry = beanEntry;
    }

    @Override
    public Class<? extends Action> getActionClass()
    {
        // cast bellow is strange but otherwise compiler will complain
        return (Class<? extends Action>) (Class<? extends Object>) beanEntry.getImplementationClass();
    }

    @Override
    public Action createNewAction()
    {
        return (Action) beanEntry.getProvider().get();
    }

    @Override
    public List<Completer> getCompleters()
    {
        if ( CompletableFunction.class.isAssignableFrom( getActionClass() ) )
        {
            return ( (CompletableFunction) createNewAction() ).getCompleters();
        }
        if ( Completable.class.isAssignableFrom( getActionClass() ) )
        {
            return (List<Completer>) ( (Completable) createNewAction() ).getCompleters();
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Completer> getOptionalCompleters()
    {
        if ( CompletableFunction.class.isAssignableFrom( getActionClass() ) )
        {
            return ( (CompletableFunction) createNewAction() ).getOptionalCompleters();
        }
        if ( Completable.class.isAssignableFrom( getActionClass() ) )
        {
            return (Map<String, Completer>) ( (Completable) createNewAction() ).getOptionalCompleters();
        }
        return Collections.emptyMap();
    }

}
