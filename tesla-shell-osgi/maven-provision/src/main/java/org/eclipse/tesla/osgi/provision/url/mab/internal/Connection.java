/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.mab.internal;

import static java.lang.String.format;
import static org.eclipse.tesla.osgi.provision.url.mab.internal.Maven2OSGiUtils.getBundleSymbolicName;
import static org.eclipse.tesla.osgi.provision.url.mab.internal.Maven2OSGiUtils.getVersion;
import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;
import static org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder.tree;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.tesla.osgi.provision.PathResolver;
import org.eclipse.tesla.osgi.provision.Storage;
import org.eclipse.tesla.osgi.provision.internal.URLAwareInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import aQute.lib.osgi.Builder;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class Connection
    extends URLConnection
{

    static final String PATH_TEMPLATE = "mab/%s";

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );

    private Storage storage;

    private PathResolver pathResolver;

    private MavenModelResolver modelResolver;

    private MavenArtifactResolver artifactResolver;

    private final MavenDependencyTreeResolver dependencyTreeResolver;

    private static final String RECIPE_COMMENT = "Created by " + Connection.class.getName();

    public Connection( final Storage storage,
                       final PathResolver pathResolver,
                       final MavenModelResolver modelResolver,
                       final MavenArtifactResolver artifactResolver,
                       final MavenDependencyTreeResolver dependencyTreeResolver,
                       final URL url )
    {
        super( url );
        this.storage = storage;
        this.pathResolver = pathResolver;
        this.modelResolver = modelResolver;
        this.artifactResolver = artifactResolver;
        this.dependencyTreeResolver = dependencyTreeResolver;
    }

    @Override
    public void connect()
        throws IOException
    {
        // ignore
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        try
        {
            final String coordinates = url.getPath();

            logger.debug( "Resolving artifact {}", coordinates );

            final Artifact artifact = artifactResolver.resolveArtifact( request().artifact( coordinates ) );
            if ( isAlreadyAnOSGiBundle( artifact.getFile() ) )
            {
                logger.debug( "Artifact {} is already an OSGi bundle. No transformation required.", coordinates );
                return new URLAwareInputStream( url, new FileInputStream( artifact.getFile() ) );
            }

            final Properties recipe = calculateRecipe( artifact, coordinates );
            final Artifact pomArtifact = pomArtifactFor( artifact );
            recipe.store( storage.outputStreamFor( pathFor( pomArtifact ) ), RECIPE_COMMENT );

            final Artifact osgiArtifact = osgiArtifactFor( artifact );
            return new URLAwareInputStream(
                url,
                createOSGiBundle( pathFor( osgiArtifact ), recipe, artifact.getFile(), coordinates )
            );
        }
        catch ( ArtifactResolutionException e )
        {
            final IOException ioException = new IOException( "Failed to resolve URL " + getURL().toExternalForm() );
            ioException.initCause( e );
            throw ioException;
        }
    }

    private DefaultArtifact pomArtifactFor( final Artifact artifact )
    {
        return new DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact.getClassifier(), "osgi",
                                    artifact.getVersion() );
    }

    private DefaultArtifact osgiArtifactFor( final Artifact artifact )
    {
        return new DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId(), "osgi", artifact.getExtension(),
                                    artifact.getVersion() );
    }

    private Properties calculateRecipe( final Artifact artifact, final String coordinates )
    {
        logger.debug( "Building OSGi transformation recipe for {}", coordinates );

        final Boolean useImportPackage = Boolean.valueOf( System.getProperty(
            getClass().getName() + ".useImportPackage", "true" )
        );
        final Boolean useRequireBundle = Boolean.valueOf( System.getProperty(
            getClass().getName() + ".useRequireBundle", "false" )
        );
        final Properties recipeProperties = new Properties();

        recipeProperties.setProperty( "Bundle-SymbolicName", getBundleSymbolicName(
            artifact.getGroupId(), artifact.getArtifactId(), artifact.getFile() )
        );
        recipeProperties.setProperty( "Bundle-Version", getVersion( artifact.getVersion() ) );
        recipeProperties.setProperty( "Import-Package", useImportPackage ? "*" : "!*" );
        recipeProperties.setProperty( "Export-Package", "*" );
        recipeProperties.setProperty( "DynamicImport-Package", "*" );
        recipeProperties.setProperty( "-nouses", "true" );

        try
        {
            logger.debug( "Resolving effective POM of {}", coordinates );
            final Model model = modelResolver.resolveModel(
                model().pom( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion() )
            );
            if ( model.getName() != null && model.getName().trim().length() > 0 )
            {
                recipeProperties.setProperty( "Bundle-Name", model.getName() );
            }
            if ( model.getDescription() != null && model.getDescription().trim().length() > 0 )
            {
                recipeProperties.setProperty( "Bundle-Description", model.getDescription() );
            }
            // TODO use license, organization, ...

            if ( useRequireBundle )
            {
                logger.debug( "Resolving dependency tree of {}", coordinates );
                final DependencyNode tree = dependencyTreeResolver.resolveDependencyTree(
                    tree().model( model().pom( coordinates ) )
                );
                final List<DependencyNode> children = tree.getChildren();
                if ( children != null )
                {
                    final StringBuilder rb = new StringBuilder();
                    for ( final DependencyNode child : children )
                    {
                        if ( !"test".equals( child.getDependency().getScope() ) )
                        {
                            final Artifact da = artifactResolver.resolveArtifact(
                                request().setArtifact( child.getDependency().getArtifact() )
                            );
                            if ( rb.length() > 0 )
                            {
                                rb.append( ", " );
                            }
                            if ( isAlreadyAnOSGiBundle( da.getFile() ) )
                            {
                                final JarFile jarFile = new JarFile( da.getFile() );
                                final Manifest manifest = jarFile.getManifest();
                                final Attributes mainAttributes = manifest.getMainAttributes();
                                rb.append( mainAttributes.getValue( "Bundle-SymbolicName" ).split( ";" )[0] );
                                rb.append( "; bundle-version=" );
                                rb.append( mainAttributes.getValue( "Bundle-Version" ) );
                                rb.append( "; resolution:=optional" );
                            }
                            else
                            {
                                rb.append( getBundleSymbolicName(
                                    da.getGroupId(), da.getArtifactId(), da.getFile() )
                                );
                                rb.append( "; bundle-version=" );
                                rb.append( getVersion( da.getVersion() ) );
                                rb.append( "; resolution:=optional" );
                            }
                        }
                    }
                    if ( rb.length() > 0 )
                    {
                        recipeProperties.put( "Require-Bundle", rb.toString() );
                    }
                }
            }
        }
        catch ( final Exception ignore )
        {
            //ignore
        }
        final Boolean useOverrides = Boolean.valueOf( System.getProperty(
            getClass().getName() + ".useOverrides", "true" )
        );
        if ( useOverrides )
        {
            final File overridesDir = new File( System.getProperty( "user.home" ), ".m2/tsh/recipes" );
            final File recipe = new File( overridesDir, pathResolver.pathFor( pomArtifactFor( artifact ) ) );
            if ( recipe.exists() )
            {
                logger.info( "Using recipe overrides from {}", recipe.getAbsolutePath() );
                InputStream in = null;
                try
                {
                    in = new FileInputStream( recipe );
                    final Properties overrides = new Properties();
                    overrides.load( in );
                    for ( Object key : overrides.keySet() )
                    {
                        recipeProperties.setProperty( (String) key, overrides.getProperty( (String) key ) );
                    }
                }
                catch ( IOException e )
                {
                    throw new RuntimeException(
                        format( "Could not merge recipe overrides %s", recipe.getAbsolutePath() ), e
                    );
                }
                finally
                {
                    IOUtil.close( in );
                }
            }
            else
            {
                logger.trace( "No recipe overrides available at {}", recipe.getAbsolutePath() );
            }
        }
        return recipeProperties;
    }

    private InputStream createOSGiBundle( final String path,
                                          final Properties recipe,
                                          final File jarFile,
                                          final String coordinates )
    {
        logger.debug( "Creating OSGi bundle for {}", coordinates );
        final Builder builder = new Builder();
        try
        {
            builder.mergeProperties( recipe, true );
            builder.setJar( jarFile );
            builder.mergeManifest( builder.getJar().getManifest() );
            builder.calcManifest();
            builder.getJar().write( storage.outputStreamFor( path ) );
            return storage.inputStreamFor( path );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( format( "OSGi bundle [%s] not created due to [%s]", path, e.getMessage() ), e );
        }
        finally
        {
            builder.close();
        }
    }

    static boolean isAlreadyAnOSGiBundle( final File file )
    {
        if ( file == null )
        {
            return false;
        }
        try
        {
            final JarFile jarFile = new JarFile( file );
            final Manifest manifest = jarFile.getManifest();
            final Attributes mainAttributes = manifest.getMainAttributes();
            return mainAttributes.getValue( "Bundle-SymbolicName" ) != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private String pathFor( final Artifact artifact )
    {
        return String.format( PATH_TEMPLATE, pathResolver.pathFor( artifact ) );
    }

}
