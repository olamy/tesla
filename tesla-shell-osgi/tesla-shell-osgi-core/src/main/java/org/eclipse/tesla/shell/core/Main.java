package org.eclipse.tesla.shell.core;

import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;
import static org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder.tree;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.inject.Inject;

import org.apache.felix.framework.util.StringMap;
import org.eclipse.tesla.shell.core.internal.PropertiesHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.locators.DefaultBeanLocator;
import org.sonatype.guice.bean.locators.MutableBeanLocator;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;
import org.sonatype.sisu.maven.bridge.support.dependency.RemoteMavenDependencyTreeResolverUsingSettings;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * TODO
 *
 * @since 1.0
 */
public class Main
{

    private String[] args;

    public Main( final String[] args )
    {
        this.args = args;
    }

    public static void main( final String[] args )
        throws Exception
    {
        new Main( args ).boot();
    }

    private void boot()
        throws Exception
    {
        final File etc4tsh = new File( System.getProperty( "shell.home" ), "etc/tsh" );
        final Properties properties = loadProperties( etc4tsh );
        final Framework framework = loadFramework( properties );
        framework.init();

        provision( new File( etc4tsh, "startup" ), framework.getBundleContext() );

        framework.start();

        while (true) {
            final FrameworkEvent event = framework.waitForStop(0);
            if (event.getType() != FrameworkEvent.STOPPED_UPDATE) {
                break;
            }
        }
    }

    private void provision( final File startup, final BundleContext bundleContext )
        throws Exception
    {
        final Maven maven = createMaven();
        if ( startup.exists() && startup.isFile() )
        {
            final BufferedReader br = new BufferedReader( new FileReader( startup ) );
            final Collection<Bundle> bundles = new ArrayList<Bundle>();
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                if ( line.trim().length() > 0 )
                {
                    final Artifact artifact = maven.artifactResolver.resolveArtifact( request().artifact( line ) );
//                    final DependencyNode node = maven.dependencyTreeResolver.resolveDependencyTree(
//                        tree().model( model().pom( line ) )
//                    );
                    bundles.add( bundleContext.installBundle( artifact.getFile().toURI().toASCIIString() ) );
                }
            }
            br.close();
            for ( final Bundle bundle : bundles )
            {
                System.out.println( bundle.getLocation() );
                try
                {
                    bundle.start();
                }
                catch ( BundleException e )
                {
                    System.out.println( "ERROR: " + e.getMessage() );
                }
            }
        }
    }

    private Collection<Bundle> install( final DependencyNode node,
                                        final BundleContext bundleContext,
                                        final Maven maven )
        throws Exception
    {
        final ArrayList<Bundle> bundles = Lists.newArrayList();
        final List<DependencyNode> children = node.getChildren();
        if ( children != null )
        {
            for ( DependencyNode child : children )
            {
                bundles.addAll( install( child, bundleContext, maven ) );
            }
        }
        final Dependency dependency = node.getDependency();
        if ( dependency != null && "compile".equalsIgnoreCase( dependency.getScope() ) )
        {
            final Artifact artifact = maven.artifactResolver.resolveArtifact(
                request().setArtifact( dependency.getArtifact() )
            );
            final Bundle bundle = bundleContext.installBundle( artifact.getFile().toURI().toASCIIString() );
            bundles.add( bundle );
        }
        return bundles;
    }

    private Maven createMaven()
    {
        final DefaultBeanLocator defaultBeanLocator = new DefaultBeanLocator();

        final Injector injector = Guice.createInjector(
            new WireModule(
                new SpaceModule( new URLClassSpace( getClass().getClassLoader() ), BeanScanning.INDEX ),
                new AbstractModule()
                {
                    @Override
                    protected void configure()
                    {
                        bind( MutableBeanLocator.class ).toInstance( defaultBeanLocator );
                        bind( MavenArtifactResolver.class ).to( RemoteMavenArtifactResolverUsingSettings.class );
                        bind( MavenDependencyTreeResolver.class ).to(
                            RemoteMavenDependencyTreeResolverUsingSettings.class );
                    }
                }
            )
        );
        defaultBeanLocator.add( injector, 0 );
        return injector.getInstance( Maven.class );
    }

    private Framework loadFramework( final Properties properties )
        throws Exception
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final InputStream is = classLoader.getResourceAsStream(
            "META-INF/services/" + FrameworkFactory.class.getName()
        );
        final BufferedReader br = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
        final String factoryClass = br.readLine();
        br.close();
        final FrameworkFactory factory = (FrameworkFactory) classLoader.loadClass( factoryClass ).newInstance();
        return factory.newFramework( new StringMap( properties, false ) );
    }

    private Properties loadProperties( final File etc )
        throws Exception
    {
        final Properties properties = PropertiesHelper.loadPropertiesFile( etc, "tsh.properties", false );
        PropertiesHelper.substituteVariables( properties );
        return properties;
    }

    public static class Maven
    {

        @Inject
        MavenArtifactResolver artifactResolver;

        @Inject
        MavenDependencyTreeResolver dependencyTreeResolver;

    }

}

