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
package org.sonatype.nexus.web.metrics;

import javax.inject.Named;

import org.sonatype.sisu.goodies.common.ComponentSupport;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

/**
 * Manages {@link HealthCheck} registrations via Sisu component mediation.
 *
 * @since 2.8
 */
@Named
public class HealthCheckMediator
    extends ComponentSupport
    implements Mediator<Named, HealthCheck, HealthCheckRegistry>
{
  public void add(final BeanEntry<Named, HealthCheck> entry, final HealthCheckRegistry registry) throws Exception {
    log.debug("Registering: {}", entry);
    registry.register(entry.getKey().value(), entry.getValue());
  }

  public void remove(final BeanEntry<Named, HealthCheck> entry, final HealthCheckRegistry registry) throws Exception {
    log.debug("Un-registering: {}", entry);
    registry.unregister(entry.getKey().value());
  }
}

