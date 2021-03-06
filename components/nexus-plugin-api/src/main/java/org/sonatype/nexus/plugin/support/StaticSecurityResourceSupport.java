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
package org.sonatype.nexus.plugin.support;

import org.sonatype.nexus.plugin.PluginIdentity;
import org.sonatype.security.realms.tools.AbstractStaticSecurityResource;
import org.sonatype.security.realms.tools.StaticSecurityResource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link StaticSecurityResource} implementations.
 *
 * @since 2.7
 */
public class StaticSecurityResourceSupport
    extends AbstractStaticSecurityResource
{
  private final PluginIdentity owner;

  public StaticSecurityResourceSupport(final PluginIdentity plugin) {
    this.owner = checkNotNull(plugin);
  }

  public String getResourcePath() {
    return String.format("/META-INF/%s-security.xml", owner.getId()); //NON-NLS
  }
}
