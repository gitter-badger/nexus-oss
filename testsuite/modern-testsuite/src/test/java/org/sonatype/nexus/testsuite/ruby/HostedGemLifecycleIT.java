/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2014 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.testsuite.ruby;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.sonatype.nexus.testsuite.ruby.TestUtils.lastLine;

public class HostedGemLifecycleIT
    extends GemLifecycleITBase
{
  public HostedGemLifecycleIT() {
    super("gemshost");
    numberOfInstalledGems = 2;
  }

  void moreAsserts(String gemName, String gemspecName, String dependencyName) {
    deleteHostedFiles(gemName, gemspecName, dependencyName);
  }

  @Test
  public void uploadGemWithPushCommand() throws Exception {
    // make sure the credentials file has the right permissions otherwise the push command fails silently
    Files.setPosixFilePermissions(testData().resolveFile(".gem/credentials").toPath(),
        PosixFilePermissions.fromString("rw-------"));

    File gem = testData().resolveFile("pre-0.1.0.beta.gem");
    assertThat(lastLine(gemRunner().push(repoId, gem)),
        equalTo("Pushing gem to http://127.0.0.1:4711/nexus/content/repositories/gemshost..."));

    assertFileDownload("gems/" + gem.getName(), is(true));
    assertFileDownload("quick/Marshal.4.8/" + gem.getName() + "spec.rz", is(true));
    assertFileDownload("api/v1/dependencies/" + gem.getName().replaceFirst("-.*$", ".json.rz"), is(true));
  }
}