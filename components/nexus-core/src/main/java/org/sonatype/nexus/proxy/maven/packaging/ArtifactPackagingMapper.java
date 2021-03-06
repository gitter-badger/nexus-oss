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
package org.sonatype.nexus.proxy.maven.packaging;

import java.io.File;

/**
 * A utility component that resolves POM packaging to artifact extension. Different implementations may provide
 * different means to do it.
 *
 * @author cstamas
 */
public interface ArtifactPackagingMapper
{
  /**
   * Returns the extension belonging to given packaging, like "jar" for "jar", "jar" for "ear", etc.
   */
  String getExtensionForPackaging(String packaging);

  /**
   * Sets the file to source the user provided mappings from, and resets the mappings, forcing it to reload the file.
   */
  void setPropertiesFile(File file);
}
