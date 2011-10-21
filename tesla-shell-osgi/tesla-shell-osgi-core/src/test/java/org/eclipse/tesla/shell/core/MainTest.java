package org.eclipse.tesla.shell.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.eclipse.tesla.shell.core.internal.StartupBundle;
import org.junit.Test;

/**
 * TODO
 *
 * @since 1.0
 */
public class MainTest
{

    @Test
    public void test()
        throws Exception
    {
        final URL resource = getClass().getClassLoader().getResource( "startup.json" );
        final Collection<StartupBundle> startupBundles = Main.loadStartupBundles( new File( resource.getFile() ) );
        assertThat( startupBundles, is( notNullValue() ) );
        assertThat( startupBundles, is( hasSize( 2 ) ) );
    }

}
