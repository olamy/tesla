package org.eclipse.tesla.shell.gshell.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.gshell.guice.CoreModule;
import com.google.inject.AbstractModule;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GuiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        install( new CoreModule() );
    }

}
