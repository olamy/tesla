package org.eclipse.tesla.osgi.provision.url.masor.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.tesla.osgi.provision.url.masor.Constants;
import org.eclipse.tesla.osgi.provision.url.masor.MavenArtifactSetObrRepository;
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

    private MavenArtifactSetObrRepository mavenArtifactSetObrRepository;

    @Inject
    HandlerService( final BundleContext bundleContext,
                    final MavenArtifactSetObrRepository mavenArtifactSetObrRepository )
    {
        this.mavenArtifactSetObrRepository = mavenArtifactSetObrRepository;

        final Properties properties = new Properties();
        properties.setProperty( URLConstants.URL_HANDLER_PROTOCOL, Constants.PROTOCOL_MASOR );
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
        return new Connection( mavenArtifactSetObrRepository, url );
    }

}
