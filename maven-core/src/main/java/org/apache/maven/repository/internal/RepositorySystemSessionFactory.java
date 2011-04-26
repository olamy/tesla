package org.apache.maven.repository.internal;

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

import org.apache.maven.execution.MavenExecutionRequest;
import org.sonatype.aether.RepositorySystemSession;

/**
 * <strong>Warning:</strong> This is an internal utility interface that is only public for technical reasons, it is not
 * part of the public API. In particular, this interface can be changed or deleted without prior notice.
 */
public interface RepositorySystemSessionFactory
{

    /**
     * Creates a new repository system session for the specified execution request.
     * 
     * @param request The execution request from which the session should be created, must not be {@code null}.
     * @return The new repository system session, never {@code null}.
     */
    public RepositorySystemSession newRepositorySession( MavenExecutionRequest request );

}
