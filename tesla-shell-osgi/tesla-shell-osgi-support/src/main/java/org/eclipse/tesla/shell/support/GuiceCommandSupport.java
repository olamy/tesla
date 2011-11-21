package org.eclipse.tesla.shell.support;

import javax.inject.Inject;

import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.eclipse.tesla.shell.ai.CommandLineParser;
import org.osgi.framework.BundleContext;

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
        return CommandLineParser.INSTANCE;
    }

}
