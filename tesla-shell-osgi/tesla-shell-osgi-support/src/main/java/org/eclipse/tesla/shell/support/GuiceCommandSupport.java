package org.eclipse.tesla.shell.support;

import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.eclipse.tesla.shell.preparator.DefaultActionPreparator;

/**
 * TODO
 *
 * @since 1.0
 */
public abstract class GuiceCommandSupport
    extends AbstractCommand
{

    @Override
    protected ActionPreparator getPreparator()
        throws Exception
    {
        return DefaultActionPreparator.INSTANCE;
    }

}
