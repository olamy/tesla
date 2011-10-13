package org.apache.maven.incremental.internal;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class MojoExecutionScope
    implements Scope
{
    private static final Provider<Object> SEEDED_KEY_PROVIDER = new Provider<Object>()
    {
        public Object get()
        {
            throw new IllegalStateException();
        }
    };

    private final ThreadLocal<Map<Key<?>, Provider<?>>> values = new ThreadLocal<Map<Key<?>, Provider<?>>>();

    public void enter()
    {
        if ( values.get() != null )
        {
            throw new IllegalStateException();
        }
        values.set( Maps.<Key<?>, Provider<?>> newHashMap() );
    }

    public void exit()
    {
        if ( values.get() == null )
        {
            throw new IllegalStateException();
        }
        values.remove();
    }

    public <T> void seed( Class<T> clazz, Provider<T> value )
    {
        values.get().put( Key.get( clazz ), value );
    }

    public <T> void seed( Class<?> clazz, final T value )
    {
        Provider<T> provider = new Provider<T>()
        {
            public T get()
            {
                return value;
            }
        };
        values.get().put( Key.get( clazz ), provider );
    }

    public <T> Provider<T> scope( final Key<T> key, final Provider<T> unscoped )
    {
        return new Provider<T>()
        {
            @SuppressWarnings( "unchecked" )
            public T get()
            {
                Map<Key<?>, Provider<?>> providers = values.get();

                if ( providers == null )
                {
                    throw new OutOfScopeException( "Cannot access " + key + " outside of a scoping block" );
                }

                Provider<?> provider = providers.get( key );

                if ( provider != null )
                {
                    return (T) provider.get();
                }

                return unscoped.get();
            }
        };
    }

    @SuppressWarnings( { "unchecked" } )
    public static <T> Provider<T> seededKeyProvider()
    {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }
}