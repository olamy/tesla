package org.eclipse.tesla.shell.support.spi;

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
public class ShellCommand
    extends AbstractCommand
    implements CompletableFunction
{

    private final BeanEntry<Annotation, Object> beanEntry;

    private final String scope;

    private final String name;

    public ShellCommand( final Command commandAnnotation, final BeanEntry<Annotation, Object> beanEntry )
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
        return (Action) beanEntry.getProvider().get();
    }

    public List<Completer> getCompleters()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    protected BeanEntry<Annotation, Object> getBeanEntry()
    {
        return beanEntry;
    }
}
