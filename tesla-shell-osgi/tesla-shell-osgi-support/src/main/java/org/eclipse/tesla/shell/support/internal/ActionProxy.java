package org.eclipse.tesla.shell.support.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
public class ActionProxy
    extends AbstractCommand
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

    public List<Completer> getCompleters()
    {
        if ( CompletableFunction.class.isAssignableFrom( getActionClass() ) )
        {
            return ( (CompletableFunction) createNewAction() ).getCompleters();
        }
        return Collections.emptyList();
    }

}