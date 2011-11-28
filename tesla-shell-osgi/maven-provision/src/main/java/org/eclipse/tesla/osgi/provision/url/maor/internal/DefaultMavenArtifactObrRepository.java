/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.maor.internal;

import static org.eclipse.tesla.osgi.provision.url.mab.Constants.PROTOCOL_MAB;
import static org.eclipse.tesla.osgi.provision.url.maor.Constants.PROTOCOL_MAOR;
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
import org.eclipse.tesla.osgi.provision.Storage;
import org.eclipse.tesla.osgi.provision.internal.IOUtils;
import org.eclipse.tesla.osgi.provision.url.maor.MavenArtifactObrRepository;
import org.sonatype.aether.artifact.Artifact;
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

    static final String OBR_PATH_TEMPLATE = "maor/%s/obr.xml";

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
            return PROTOCOL_MAOR + ":" + coordinates;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Resource resource( final String coordinates )
    {
        final ResourceImpl resource;
        try
        {
            resource = (ResourceImpl) dataModelHelper.createResource(
                new URL( PROTOCOL_MAB + ":" + coordinates )
            );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create resource for " + coordinates, e );
        }
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
            final Artifact artifact = tree.getDependency().getArtifact();
            // skip osgi core dependency
            if ( artifact.getGroupId().equals( "org.osgi" ) && artifact.getArtifactId().equals( "org.osgi.core" ) )
            {
                return resources;
            }
            final Resource resource = resource( artifact.toString() );
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
