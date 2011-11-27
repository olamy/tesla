package org.eclipse.tesla.shell.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.eclipse.tesla.shell.support.GuiceCommandSupport;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
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
        return Collections.emptyList();
    }

    @Override
    public Map<String, Completer> getOptionalCompleters()
    {
        if ( CompletableFunction.class.isAssignableFrom( getActionClass() ) )
        {
            return ( (CompletableFunction) createNewAction() ).getOptionalCompleters();
        }
        return Collections.emptyMap();
    }

}
