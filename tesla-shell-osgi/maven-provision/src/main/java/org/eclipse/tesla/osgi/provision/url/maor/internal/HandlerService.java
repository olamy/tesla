package org.eclipse.tesla.osgi.provision.url.maor.internal;

import static org.eclipse.tesla.osgi.provision.url.maor.Constants.PROTOCOL_MAOR;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.tesla.osgi.provision.url.maor.MavenArtifactObrRepository;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.sonatype.inject.EagerSingleton;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@EagerSingleton
public class HandlerService
    extends AbstractURLStreamHandlerService
{

    private MavenArtifactObrRepository mavenArtifactObrRepository;

    @Inject
    HandlerService( final BundleContext bundleContext,
                    final MavenArtifactObrRepository mavenArtifactObrRepository )
    {
        this.mavenArtifactObrRepository = mavenArtifactObrRepository;

        final Properties properties = new Properties();
        properties.setProperty( URLConstants.URL_HANDLER_PROTOCOL, PROTOCOL_MAOR );
        bundleContext.registerService(
            URLStreamHandlerService.class.getName(),
            this,
            properties
        );
    }

    @Override
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        return new Connection( mavenArtifactObrRepository, url );
    }

}
