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

package org.sonatype.maven.shell.commands.maven;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.gshell.command.support.CommandTestSupport;

/**
 * Tests for the {@link MavenCommand}.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MavenCommandTest extends CommandTestSupport {
  public MavenCommandTest() {
    super(MavenCommand.class);
  }

  @Override
  @Test
  @Ignore
  public void testDefault() throws Exception {
    // disabled
  }

  @Test
  public void test1() throws Exception {
    /*
      
    TODO: This doesn't work on Ubuntu 11.04 
     
    String settings = System.getProperty("hostEnvSettings");
    if (settings == null || new File(settings).exists() == false) {
      settings = new File(getClass().getResource("settings.xml").toURI()).toString();
    }
    System.out.println("Settings: " + settings);

    String pom = new File(getClass().getResource("test1.pom").toURI()).toString();
    System.out.println("POM: " + pom);

    Object result = executeWithArgs("-B", "-e", "-V", "-f", pom, "-s", settings, "package");

    System.out.println("OUT: " + getIo().getOutputString());
    System.out.println("ERR: " + getIo().getErrorString());

    assertEquals(0, result);
    */
  }
}