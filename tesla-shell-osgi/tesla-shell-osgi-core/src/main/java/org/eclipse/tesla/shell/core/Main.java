package org.eclipse.tesla.shell.core;

import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.inject.Inject;

import org.apache.felix.framework.util.StringMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.eclipse.tesla.shell.core.internal.PropertiesHelper;
import org.eclipse.tesla.shell.core.internal.StartupBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.startlevel.StartLevel;
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
import org.sonatype.sisu.maven.bridge.Names;
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

        System.setProperty( "logback.configurationFile", new File( etc4tsh, "logback.xml" ).getAbsolutePath() );

        final Properties properties = loadProperties( etc4tsh );
        final Framework framework = loadFramework( properties );
        framework.init();

        provision( new File( etc4tsh, "startup.json" ), framework.getBundleContext(), properties );

        framework.start();

        while ( true )
        {
            final FrameworkEvent event = framework.waitForStop( 0 );
            if ( event.getType() != FrameworkEvent.STOPPED_UPDATE )
            {
                break;
            }
        }
    }

    private void provision( final File startup, final BundleContext bundleContext, final Properties properties )
        throws Exception
    {
        final Maven maven = createMaven();

        final StartLevel sl = (StartLevel) bundleContext.getService(
            bundleContext.getServiceReference( StartLevel.class.getName() )
        );
        int ibsl = 60;
        try
        {
            final String str = properties.getProperty( "karaf.startlevel.bundle" );
            if ( str != null )
            {
                ibsl = Integer.parseInt( str );
            }
        }
        catch ( Exception ignore )
        {
        }
        sl.setInitialBundleStartLevel( ibsl );

        final Collection<Bundle> bundles = new ArrayList<Bundle>();
        for ( final StartupBundle startupBundle : loadStartupBundles( startup ) )
        {
            final Artifact artifact = maven.artifactResolver.resolveArtifact(
                request().artifact( startupBundle.getCoordinates() )
            );
//            final DependencyNode node = maven.dependencyTreeResolver.resolveDependencyTree(
//                tree().model( model().pom( line ) )
//            );
            final Bundle bundle = bundleContext.installBundle( artifact.getFile().toURI().toASCIIString() );
            bundles.add( bundle );
            sl.setBundleStartLevel( bundle, startupBundle.getStartLevel() < 1 ? ibsl : startupBundle.getStartLevel() );
        }

        for ( Bundle bundle : bundles )
        {
            try
            {
                final String fragmentHostHeader = (String) bundle.getHeaders().get( Constants.FRAGMENT_HOST );
                if ( fragmentHostHeader == null || fragmentHostHeader.trim().length() == 0 )
                {
                    bundle.start();
                }
            }
            catch ( Exception ex )
            {
                System.err.println( "Error starting bundle " + bundle.getSymbolicName() + ": " + ex );
            }
        }
    }

    // @TestAccessible
    static Collection<StartupBundle> loadStartupBundles( final File startup )
        throws Exception
    {
        Collection<StartupBundle> startupBundles = null;
        if ( startup.exists() && startup.isFile() )
        {
            final ObjectMapper mapper = new ObjectMapper();
            InputStream in = null;
            try
            {
                in = new BufferedInputStream( new FileInputStream( startup ) );
                startupBundles = mapper.readValue( in, TypeFactory.defaultInstance().constructCollectionType(
                    ArrayList.class, StartupBundle.class )
                );
            }
            catch ( final Exception e )
            {
                throw new RuntimeException( e );
            }
            finally
            {
                if ( in != null )
                {
                    in.close();
                }
            }
        }
        if ( startupBundles == null )
        {
            return Lists.newArrayList();
        }
        return startupBundles;
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
        if ( properties.getProperty( Constants.FRAMEWORK_STORAGE ) == null )
        {
            File storage = new File( Names.MAVEN_USER_HOME, "tsh/cache" );
            try
            {
                storage.mkdirs();
            }
            catch ( SecurityException se )
            {
                throw new Exception( se.getMessage() );
            }
            properties.setProperty( Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath() );
        }

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

