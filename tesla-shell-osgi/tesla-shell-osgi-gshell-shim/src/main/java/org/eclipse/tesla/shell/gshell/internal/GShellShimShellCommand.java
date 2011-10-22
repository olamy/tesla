package org.eclipse.tesla.shell.gshell.internal;

import java.lang.annotation.Annotation;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.karaf.shell.console.CompletableFunction;
import org.eclipse.tesla.shell.support.spi.ShellCommand;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.shell.Shell;
import org.sonatype.inject.BeanEntry;

/**
 * TODO
 *
 * @since 1.0
 */
class GShellShimShellCommand
    extends ShellCommand
    implements CompletableFunction
{

    private final Command commandAnnotation;

    GShellShimShellCommand( final Command commandAnnotation,
                            final BeanEntry<Annotation, Object> beanEntry)
    {
        super( commandAnnotation, beanEntry );
        this.commandAnnotation = commandAnnotation;
    }

    @Override
    public Action createNewAction()
    {
        final BeanEntry<Annotation, Object> beanEntry = getBeanEntry();
        final CommandAction commandAction = (CommandAction) beanEntry.getProvider().get();
        return new GShellShimAction( commandAction );
    }

    @Override
    protected ActionPreparator getPreparator()
        throws Exception
    {
        return new GShellShimActionPreparator(commandAnnotation);
    }



}
