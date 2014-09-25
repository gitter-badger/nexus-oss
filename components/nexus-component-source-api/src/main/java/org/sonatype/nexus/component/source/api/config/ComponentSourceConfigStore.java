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

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * A persistent store of {@link ComponentSourceConfig} objects.
 *
 * @since 3.0
 */
public interface ComponentSourceConfigStore
{
  /**
   * Adds a config
   *
   * @param config to be added
   * @throws IOException IOException If any problem encountered while read/store of config storage
   */
  ComponentConfigId add(ComponentSourceConfig config) throws IOException;

  /**
   * Updates stored config if exists.
   *
   * @param config to be updated
   * @throws IOException If any problem encountered while read/store of config storage
   */
  void update(ComponentConfigId id, ComponentSourceConfig config) throws IOException;

  /**
   * Deletes stored config if exists.
   *
   * @param id of config to be deleted
   * @return false if config to be deleted does not exist in storage, true otherwise
   * @throws IOException If any problem encountered while read/store of config storage
   */
  void remove(ComponentConfigId id) throws IOException;

  /**
   * Retrieves stored configs.
   *
   * @return configs (never null)
   * @throws IOException If any problem encountered while read/store of config storage
   */
  Map<ComponentConfigId, ComponentSourceConfig> getAll() throws IOException;


  /**
   * Retrieves a config based on its name.
   *
   * @return null if there's no config by that name.
   */
  @Nullable
  ComponentSourceConfig get(String sourceName) throws IOException;
}
