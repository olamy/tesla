package org.eclipse.tesla.shell.provision.url.maor.internal;

import static org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder.tree;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.DataModelHelperImpl;
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

    private Storage storage;

    private MavenDependencyTreeResolver dependencyTreeResolver;

    @Inject
    public DefaultMavenArtifactObrRepository( final Storage storage,
                                              final MavenDependencyTreeResolver dependencyTreeResolver )
    {
        this.storage = storage;
        this.dependencyTreeResolver = dependencyTreeResolver;
    }

    @Override
    public String create( final String coordinates )
    {
        try
        {
            final DependencyNode tree = dependencyTreeResolver.resolveDependencyTree(
                tree().model( model().pom( coordinates ) )
            );
            final List<Resource> resources = asResources( tree, new DataModelHelperImpl() );

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

    private List<Resource> asResources( final DependencyNode tree, final DataModelHelper modelHelper )
        throws IOException
    {
        final List<Resource> resources = new ArrayList<Resource>();
        if ( tree.getChildren() != null )
        {
            for ( DependencyNode child : tree.getChildren() )
            {
                resources.addAll( asResources( child, modelHelper ) );
            }
        }
        if ( tree.getDependency() != null )
        {
            final Resource resource = modelHelper.createResource(
                new URL( "mab:" + tree.getDependency().getArtifact().toString() )
            );
            if ( resource != null )
            {
                resources.add( resource );
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
