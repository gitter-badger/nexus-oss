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
package org.sonatype.security.ldap.realms.persist;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.crypto.CryptoHelper;
import org.sonatype.sisu.goodies.crypto.maven.PasswordCipherMavenLegacyImpl;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is "legacy" component to be used in upgrades only. This component uses non-"shielded" PasswordCipher directly of
 * legacy PlexusCipher.
 * 
 * @deprecated To be used in configuration upgrades only.
 */
@Deprecated
@Singleton
@Named
public class DefaultPasswordHelper
    implements PasswordHelper
{

  private static final String ENC = "CMMDwoV";

  private final PasswordCipherMavenLegacyImpl mavenLegacyPasswordCipher;

  @Inject
  public DefaultPasswordHelper(final CryptoHelper cryptoHelper) {
    checkNotNull(cryptoHelper, "cryptoHelper");
    this.mavenLegacyPasswordCipher = new PasswordCipherMavenLegacyImpl(cryptoHelper);
  }

  public String encrypt(String password)
  {
    if (password != null) {
      return new String(mavenLegacyPasswordCipher.encrypt(password.getBytes(Charsets.UTF_8), ENC), Charsets.UTF_8);
    }
    return null;
  }

  public String decrypt(String encodedPassword)
  {
    if (encodedPassword != null) {
      return new String(mavenLegacyPasswordCipher.decrypt(encodedPassword.getBytes(Charsets.UTF_8), ENC), Charsets.UTF_8);
    }
    return null;
  }
}
