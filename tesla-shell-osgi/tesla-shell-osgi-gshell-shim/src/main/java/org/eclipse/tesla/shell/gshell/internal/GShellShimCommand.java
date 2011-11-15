package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.eclipse.tesla.shell.gshell.internal.preparator.GShellShimCommandLineParser;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.util.NameAware;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
class GShellShimCommand
    extends AbstractCommand
    implements CompletableFunction
{

    private final Command commandAnnotation;

    private BeanEntry<Annotation, Object> beanEntry;

    GShellShimCommand( final Command commandAnnotation,
                       final BeanEntry<Annotation, Object> beanEntry )
    {
        this.commandAnnotation = commandAnnotation;
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
            String name = commandAnnotation.name();
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
        return new GShellShimCommandLineParser( commandAnnotation );
    }

    public List<Completer> getCompleters()
    {
        // TODO
        return null;
    }
}
