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
package org.sonatype.nexus.analytics;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link RollingCounter}.
 */
public class RollingCounterTest
  extends TestSupport
{
  @Test
  public void basics() {
    RollingCounter underTest = new RollingCounter(5);
    assertThat(underTest.next(), is(0L));
    assertThat(underTest.next(), is(1L));
    assertThat(underTest.next(), is(2L));
    assertThat(underTest.next(), is(3L));
    assertThat(underTest.next(), is(4L));
    assertThat(underTest.next(), is(5L));

    // rolls
    assertThat(underTest.next(), is(0L));
    assertThat(underTest.next(), is(1L));
    assertThat(underTest.next(), is(2L));
    assertThat(underTest.next(), is(3L));
    assertThat(underTest.next(), is(4L));
    assertThat(underTest.next(), is(5L));

    // rolls
    assertThat(underTest.next(), is(0L));
    assertThat(underTest.next(), is(1L));
    assertThat(underTest.next(), is(2L));
    assertThat(underTest.next(), is(3L));
    assertThat(underTest.next(), is(4L));
    assertThat(underTest.next(), is(5L));
  }

  @Test(expected = IllegalArgumentException.class)
  public void maxTooSmall() {
    new RollingCounter(0);
  }
}
