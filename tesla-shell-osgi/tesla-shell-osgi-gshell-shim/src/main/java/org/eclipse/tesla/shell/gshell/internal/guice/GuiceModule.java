/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.gshell.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.gshell.guice.CoreModule;
import com.google.inject.AbstractModule;

/**
 * TODO
 *
 * @since 1.0
 */
@Named
@Singleton
public class GuiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        install( new CoreModule() );
    }

}
