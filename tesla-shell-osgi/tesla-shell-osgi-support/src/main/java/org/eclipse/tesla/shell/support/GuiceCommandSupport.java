/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.shell.support;

import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.apache.karaf.shell.commands.basic.ActionPreparator;
import org.eclipse.tesla.shell.preparator.DefaultActionPreparator;

/**
 * TODO
 *
 * @since 1.0
 */
public abstract class GuiceCommandSupport
    extends AbstractCommand
{

    @Override
    protected ActionPreparator getPreparator()
        throws Exception
    {
        return DefaultActionPreparator.INSTANCE;
    }

}
