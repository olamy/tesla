package org.eclipse.tesla.shell.commands.support.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
class GuiceShellCommand
    extends AbstractCommand
    implements CompletableFunction
{

    private final BeanEntry<Annotation, Action> beanEntry;

    private final String scope;

    private final String name;

    GuiceShellCommand( final Command commandAnnotation, final BeanEntry<Annotation, Action> beanEntry )
    {
        this.beanEntry = beanEntry;
        scope = commandAnnotation.scope();
        name = commandAnnotation.name();
    }

    public String getScope()
    {
        return scope;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public Action createNewAction()
    {
        return beanEntry.getProvider().get();
    }

    public List<Completer> getCompleters()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
