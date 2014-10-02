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
package org.sonatype.nexus.component.services.query;

import java.util.List;

import org.sonatype.nexus.component.model.Asset;
import org.sonatype.nexus.component.model.Component;

/**
 * Search interface for finding components and assets.
 *
 * Note that the returned lists consist of POJOs that represent a snapshot of the state of the underlying records at
 * the time of the query. They are thus divorced from the underlying session scope.
 *
 * @since 3.0
 */
public interface MetadataQueryService
{
  <T extends Component> long countComponents(Class<T> componentClass, QueryExpression queryExpression);

  long countAssets(QueryExpression queryExpression);

  <T extends Component> List<T> findComponents(Class<T> componentClass, MetadataQuery metadataQuery);

  List<Asset> findAssets(MetadataQuery metadataQuery);
}