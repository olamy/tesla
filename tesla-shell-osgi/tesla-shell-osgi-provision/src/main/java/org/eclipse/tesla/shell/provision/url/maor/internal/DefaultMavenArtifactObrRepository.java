package org.eclipse.tesla.shell.provision.url.maor.internal;

import static org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder.tree;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.DataModelHelperImpl;
import org.apache.felix.bundlerepository.impl.ResourceImpl;
import org.eclipse.tesla.shell.provision.Storage;
import org.eclipse.tesla.shell.provision.internal.IOUtils;
import org.eclipse.tesla.shell.provision.url.maor.MavenArtifactObrRepository;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
public class DefaultMavenArtifactObrRepository
    implements MavenArtifactObrRepository
{

    static final String OBR_PATH_TEMPLATE = "obr/maor/%s/obr.xml";

    private final Storage storage;

    private final MavenDependencyTreeResolver dependencyTreeResolver;

    private final DataModelHelperImpl dataModelHelper;

    @Inject
    public DefaultMavenArtifactObrRepository( final Storage storage,
                                              final MavenDependencyTreeResolver dependencyTreeResolver )
    {
        this.storage = storage;
        this.dependencyTreeResolver = dependencyTreeResolver;
        this.dataModelHelper = new DataModelHelperImpl();
    }

    @Override
    public String create( final String coordinates )
    {
        try
        {
            final DependencyNode tree = dependencyTreeResolver.resolveDependencyTree(
                tree().model( model().pom( coordinates ) ) );

            final List<Resource> resources = new ArrayList<Resource>();
            resources.add( resource( coordinates ) );
            resources.addAll( asResources( tree ) );

            final DataModelHelper helper = new DataModelHelperImpl();
            final Repository repository = helper.repository( resources.toArray( new Resource[resources.size()] ) );

            Writer out = null;
            try
            {
                out = new OutputStreamWriter( storage.outputStreamFor( obrPathFor( coordinates ) ) );
                helper.writeRepository( repository, out );
            }
            finally
            {
                IOUtils.close( out );
            }
            return MAOR_PROTOCOL + ":" + coordinates;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Resource resource( final String coordinates )
        throws IOException
    {
        final ResourceImpl resource = (ResourceImpl) dataModelHelper.createResource( new URL( "mab:" + coordinates ) );
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put( "maven-coordinates", coordinates );
        resource.addCapability( dataModelHelper.capability( "maven", properties ) );
        return resource;
    }

    private List<Resource> asResources( final DependencyNode tree )
        throws IOException
    {
        final List<Resource> resources = new ArrayList<Resource>();
        if ( tree.getDependency() != null )
        {
            // skip test resources
            if ( "test".equals( tree.getDependency().getScope() ) )
            {
                return resources;
            }
            final Resource resource = resource( tree.getDependency().getArtifact().toString() );
            if ( resource != null )
            {
                resources.add( resource );
            }
        }
        if ( tree.getChildren() != null )
        {
            for ( DependencyNode child : tree.getChildren() )
            {
                resources.addAll( asResources( child ) );
            }
        }
        return resources;
    }

    @Override
    public InputStream openStream( final String path )
    {
        return storage.inputStreamFor( obrPathFor( path ) );
    }

    private String obrPathFor( final String path )
    {
        return String.format( OBR_PATH_TEMPLATE, path );
    }

}
