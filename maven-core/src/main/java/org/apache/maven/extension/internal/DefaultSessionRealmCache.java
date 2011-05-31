package org.apache.maven.extension.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Default session realm cache implementation. Assumes cached data does not change.
 */
@Component( role = SessionRealmCache.class )
public class DefaultSessionRealmCache
    implements SessionRealmCache
{

    private static class CacheKey
        implements Key
    {

        private final List<? extends ClassLoader> extensionRealms;

        private final int hashCode;

        public CacheKey( List<? extends ClassLoader> extensionRealms )
        {
            this.extensionRealms = ( extensionRealms != null ) ? extensionRealms : Collections.<ClassRealm> emptyList();

            this.hashCode = this.extensionRealms.hashCode();
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( o == this )
            {
                return true;
            }

            if ( !( o instanceof CacheKey ) )
            {
                return false;
            }

            CacheKey other = (CacheKey) o;

            return extensionRealms.equals( other.extensionRealms );
        }

        @Override
        public String toString()
        {
            return extensionRealms.toString();
        }

    }

    private final Map<Key, CacheRecord> cache = new HashMap<Key, CacheRecord>();

    public Key createKey( List<? extends ClassRealm> extensionRealms )
    {
        return new CacheKey( extensionRealms );
    }

    public CacheRecord get( Key key )
    {
        return cache.get( key );
    }

    public CacheRecord put( Key key, ClassRealm sessionRealm )
    {
        if ( sessionRealm == null )
        {
            throw new NullPointerException();
        }

        if ( cache.containsKey( key ) )
        {
            throw new IllegalStateException( "Duplicate session realm for extensions " + key );
        }

        CacheRecord record = new CacheRecord( sessionRealm );

        cache.put( key, record );

        return record;
    }

    public void flush()
    {
        cache.clear();
    }

}
