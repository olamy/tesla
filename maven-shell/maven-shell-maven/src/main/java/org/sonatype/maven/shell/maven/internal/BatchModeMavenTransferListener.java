/*******************************************************************************
 * Copyright (c) 2009-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at 
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses. 
 *******************************************************************************/

package org.sonatype.maven.shell.maven.internal;

import java.io.PrintStream;

/**
 * Batch console transfer listener.
 * 
 * @since 0.9
 */
public class BatchModeMavenTransferListener extends AbstractMavenTransferListener {
  public BatchModeMavenTransferListener(final PrintStream out) {
    super(out);
  }
}