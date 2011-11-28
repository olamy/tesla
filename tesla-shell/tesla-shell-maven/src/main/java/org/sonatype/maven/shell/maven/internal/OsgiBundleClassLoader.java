/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.sonatype.maven.shell.maven.internal;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
public class OsgiBundleClassLoader
    extends ClassLoader
{

    private static final EmptyEnumeration<URL> EMPTY_URL_ENUMERATION = new EmptyEnumeration<URL>();

    private static final class EmptyEnumeration<T>
        implements Enumeration<T>
    {

        public boolean hasMoreElements()
        {
            return false;
        }

        public T nextElement()
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Bundle used for class loading.
     */
    private final Bundle m_bundle;

    /**
     * Privileged factory method.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     * @return created bundle class loader
     * @see OsgiBundleClassLoader#OsgiBundleClassLoader(Bundle)
     */
    public static OsgiBundleClassLoader newPriviledged( final Bundle bundle )
    {
        return newPriviledged( bundle, null );
    }

    /**
     * Privileged factory method.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     * @param parent parent class loader
     * @return created bundle class loader
     * @see OsgiBundleClassLoader#OsgiBundleClassLoader(Bundle, ClassLoader)
     */
    public static OsgiBundleClassLoader newPriviledged( final Bundle bundle, final ClassLoader parent )
    {
        return AccessController.doPrivileged( new PrivilegedAction<OsgiBundleClassLoader>()
        {
            public OsgiBundleClassLoader run()
            {
                return new OsgiBundleClassLoader( bundle, parent );
            }
        } );
    }

    /**
     * Creates a bundle class loader with no parent.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     */
    public OsgiBundleClassLoader( final Bundle bundle )
    {
        this( bundle, null );
    }

    /**
     * Creates a bundle class loader.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     * @param parent parent class loader
     */
    public OsgiBundleClassLoader( final Bundle bundle, final ClassLoader parent )
    {
        super( parent );
        m_bundle = bundle;
    }

    /**
     * Getter.
     *
     * @return the bundle the class loader loads from
     */
    public Bundle getBundle()
    {
        return m_bundle;
    }

    /**
     * If there is a parent class loader use the super implementation that will first use the parent and as a fallback
     * it will call findResource(). In case there is no parent directy use findResource() as if we call the super
     * implementation it will use the VMClassLoader, fact that should be avoided.
     *
     * @see ClassLoader#getResource(String)
     */
    @Override
    public URL getResource( final String name )
    {
        if ( getParent() != null )
        {
            return super.getResource( name );
        }
        return findResource( name );
    }

    /**
     * If there is a parent class loader use the super implementation that will first use the parent and as a fallback
     * it will call findResources(). In case there is no parent directy use findResources() as if we call the super
     * implementation it will use the VMClassLoader, fact that should be avoided.
     *
     * @see ClassLoader#getResources(String)
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public Enumeration<URL> getResources( final String name )
        throws IOException
    {
        if ( getParent() != null )
        {
            return super.getResources( name );
        }
        else
        {
            return findResources( name );
        }
    }

    /**
     * Use bundle to find find the class.
     *
     * @see ClassLoader#findClass(String)
     */
    @Override
    protected Class findClass( final String name )
        throws ClassNotFoundException
    {
        return m_bundle.loadClass( name );
    }

    /**
     * If there is a parent class loader use the super implementation that will first use the parent and as a fallback
     * it will call findClass(). In case there is no parent directy use findClass() as if we call the super
     * implementation it will use the VMClassLoader, fact that should be avoided.
     *
     * @see ClassLoader#getResource(String)
     */
    @Override
    protected Class loadClass( final String name, final boolean resolve )
        throws ClassNotFoundException
    {
        if ( getParent() != null )
        {
            return super.loadClass( name, resolve );
        }
        final Class classToLoad = findClass( name );
        if ( resolve )
        {
            resolveClass( classToLoad );
        }
        return classToLoad;
    }

    /**
     * Use bundle to find resource.
     *
     * @see ClassLoader#findResource(String)
     */
    @Override
    protected URL findResource( final String name )
    {
        return m_bundle.getResource( name );
    }

    /**
     * Use bundle to find resources.
     *
     * @see ClassLoader#findResources(String)
     */
    @Override
    @SuppressWarnings( "unchecked" )
    protected Enumeration<URL> findResources( final String name )
        throws IOException
    {
        Enumeration resources = m_bundle.getResources( name );
        // Bundle.getResources may return null, in such case return empty enumeration
        if ( resources == null )
        {
            return EMPTY_URL_ENUMERATION;
        }
        else
        {
            return resources;
        }
    }

    @Override
    public String toString()
    {
        return new StringBuffer().append( this.getClass().getSimpleName() ).append( "{" ).append( "bundle=" ).append(
            m_bundle ).append( ",parent=" ).append( getParent() ).append( "}" ).toString();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        OsgiBundleClassLoader that = (OsgiBundleClassLoader) o;

        if ( m_bundle != null ? !m_bundle.equals( that.m_bundle ) : that.m_bundle != null )
        {
            return false;
        }

        if ( getParent() != null ? !getParent().equals( that.getParent() ) : that.getParent() != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return ( m_bundle != null ? m_bundle.hashCode() : 0 ) * 37 + ( getParent() != null
            ? getParent().hashCode()
            : 0 );
    }
}