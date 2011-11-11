package org.eclipse.tesla.shell.provision.url.mab.internal;

import static org.eclipse.tesla.shell.provision.url.mab.Constants.PROTOCOL_MAB;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.tesla.shell.provision.PathResolver;
import org.eclipse.tesla.shell.provision.Storage;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.sonatype.inject.EagerSingleton;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;

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

    private final Storage storage;

    private final PathResolver pathResolver;

    private final MavenModelResolver modelResolver;

    private final MavenArtifactResolver artifactResolver;

    private final MavenDependencyTreeResolver dependencyTreeResolver;

    @Inject
    HandlerService( final BundleContext bundleContext,
                    final Storage storage,
                    final PathResolver pathResolver,
                    final MavenModelResolver modelResolver,
                    final MavenArtifactResolver artifactResolver,
                    final MavenDependencyTreeResolver dependencyTreeResolver )
    {
        this.storage = storage;
        this.pathResolver = pathResolver;
        this.modelResolver = modelResolver;
        this.artifactResolver = artifactResolver;
        this.dependencyTreeResolver = dependencyTreeResolver;

        final Properties properties = new Properties();
        properties.setProperty( URLConstants.URL_HANDLER_PROTOCOL, PROTOCOL_MAB );
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
        return new Connection( storage, pathResolver, modelResolver, artifactResolver, dependencyTreeResolver, url );
    }

}
