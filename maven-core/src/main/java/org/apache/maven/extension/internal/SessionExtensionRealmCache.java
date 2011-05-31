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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.aether.artifact.Artifact;

/**
 * Caches session extension class realms. <strong>Warning:</strong> This is an internal utility interface that is only
 * public for technical reasons, it is not part of the public API. In particular, this interface can be changed or
 * deleted without prior notice.
 * 
 * @author Benjamin Bentmann
 */
public interface SessionExtensionRealmCache
{

    /**
     * A cache key.
     */
    interface Key
    {
        // marker interface for cache keys
    }

    public static class CacheRecord
    {

        public final ClassRealm realm;

        public CacheRecord( ClassRealm realm )
        {
            this.realm = realm;
        }

    }

    Key createKey( List<? extends Artifact> extensionArtifacts );

    CacheRecord get( Key key );

    CacheRecord put( Key key, ClassRealm extensionRealm );

    void flush();

}
