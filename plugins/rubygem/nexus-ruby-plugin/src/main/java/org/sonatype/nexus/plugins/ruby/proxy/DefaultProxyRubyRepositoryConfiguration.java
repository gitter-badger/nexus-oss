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
package org.sonatype.nexus.plugins.ruby.proxy;

import org.sonatype.nexus.proxy.repository.AbstractProxyRepositoryConfiguration;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class DefaultProxyRubyRepositoryConfiguration
    extends AbstractProxyRepositoryConfiguration
{
  public static final String ARTIFACT_MAX_AGE = "artifactMaxAge";

  public static final String METADATA_MAX_AGE = "metadataMaxAge";

  public DefaultProxyRubyRepositoryConfiguration(Xpp3Dom configuration) {
    super(configuration);
  }

  public int getArtifactMaxAge() {
    return Integer.parseInt(getNodeValue(getRootNode(), ARTIFACT_MAX_AGE, "-1"));
  }

  public void setArtifactMaxAge(int age) {
    setNodeValue(getRootNode(), ARTIFACT_MAX_AGE, String.valueOf(age));
  }

  public int getMetadataMaxAge() {
    return Integer.parseInt(getNodeValue(getRootNode(), METADATA_MAX_AGE, "30"));
  }

  public void setMetadataMaxAge(int age) {
    setNodeValue(getRootNode(), METADATA_MAX_AGE, String.valueOf(age));
  }
}
