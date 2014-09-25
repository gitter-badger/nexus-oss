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
package org.sonatype.nexus.component.source.api.config;

import java.util.Map;

import org.sonatype.nexus.component.source.api.ComponentSource;

import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Configuration for a {@link ComponentSource}.
 *
 * @since 3.0
 */
public class ComponentSourceConfig
{
  private final String sourceName;

  private final String factoryName;

  private final Map<String, Object> configuration;

  public ComponentSourceConfig(final String sourceName, final String factoryName,
                               final Map<String, Object> configuration)
  {
    this.sourceName = checkNotNull(sourceName);
    this.factoryName = checkNotNull(factoryName);
    this.configuration = ImmutableMap.copyOf(checkNotNull(configuration));
  }

  public String getSourceName() {
    return sourceName;
  }

  public String getFactoryName() {
    return factoryName;
  }

  public Map<String, Object> getConfiguration() {
    return configuration;
  }

  @Override
  public String toString() {
    return "ComponentSourceConfig{" +
        "sourceName='" + sourceName + '\'' +
        ", factoryName='" + factoryName + '\'' +
        '}';
  }
}
