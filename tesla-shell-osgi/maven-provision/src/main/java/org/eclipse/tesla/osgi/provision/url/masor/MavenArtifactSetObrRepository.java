/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.masor;

import java.io.InputStream;

/**
 * TODO
 *
 * @since 1.0
 */
public interface MavenArtifactSetObrRepository
{

    String create( String... coordinates );

    InputStream openStream( String path );

}
