/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision;

import java.io.PrintStream;

import org.osgi.framework.Bundle;

/**
 * TODO
 *
 * @since 1.0
 */
public interface ProvisionSet
{

    Bundle[] install();

    Bundle[] installAndStart( );

    boolean hasProblems();

    void printProblems( final PrintStream err );

}
