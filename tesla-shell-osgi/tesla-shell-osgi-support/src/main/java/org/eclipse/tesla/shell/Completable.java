/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell;

import java.util.List;
import java.util.Map;

import org.apache.karaf.shell.console.Completer;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Completable
{

    List<? extends Completer> getCompleters();

    Map<String, ? extends Completer> getOptionalCompleters();

}
