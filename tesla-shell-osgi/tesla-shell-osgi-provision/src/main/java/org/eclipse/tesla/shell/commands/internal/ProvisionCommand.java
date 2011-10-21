package org.eclipse.tesla.shell.commands.internal;

import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;

import java.net.URI;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.eclipse.tesla.shell.commands.support.GuiceOsgiCommandSupport;
import org.osgi.framework.Bundle;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Command( scope = "provision", name = "install", description = "Provision jars" )
public class ProvisionCommand
    extends GuiceOsgiCommandSupport
{

    @Argument( name = "coordinates", description = "Maven coordinates of jar to be provisioned", required = true )
    private String coordinates;

    @Option( name = "-s", aliases = { "--start" }, description = "If provisioned bundle should be started" )
    private boolean start;

    @Option( name = "-sl", aliases = { "--startLevel" }, description = "Start level for provisioned bundle" )
    private int startLevel;

    @Inject
    private MavenArtifactResolver resolver;

    @Override
    protected Object doExecute()
        throws Exception
    {
        final Artifact artifact = resolver.resolveArtifact( request().artifact( coordinates ) );
        final URI uri = artifact.getFile().toURI();
        final Bundle bundle = getBundleContext().installBundle( uri.toASCIIString(), uri.toURL().openStream() );
        if ( start )
        {
            bundle.start();
        }
        return bundle;
    }

}
