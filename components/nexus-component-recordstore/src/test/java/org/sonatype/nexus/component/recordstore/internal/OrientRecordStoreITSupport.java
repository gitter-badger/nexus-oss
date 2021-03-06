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
package org.sonatype.nexus.component.recordstore.internal;

import java.math.BigDecimal;
import java.util.Date;

import org.sonatype.nexus.component.recordstore.FieldDefinition;
import org.sonatype.nexus.component.recordstore.RecordId;
import org.sonatype.nexus.component.recordstore.RecordStoreSchema;
import org.sonatype.nexus.component.recordstore.RecordStoreSession;
import org.sonatype.nexus.component.recordstore.RecordType;
import org.sonatype.nexus.internal.orient.HexRecordIdObfuscator;
import org.sonatype.nexus.internal.orient.MemoryDatabaseManager;
import org.sonatype.nexus.internal.orient.MinimalDatabaseServer;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.orientechnologies.common.log.OLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class OrientRecordStoreITSupport
    extends TestSupport
{
  protected static final BigDecimal bigDecimalValue = new BigDecimal(1);
  protected static final Byte byteValue = Byte.MAX_VALUE;
  protected static final Date dateValue = new Date(42);
  protected static final Double doubleValue = Double.MAX_VALUE;
  protected static final Float floatValue = Float.MAX_VALUE;
  protected static final Integer integerValue = Integer.MAX_VALUE;
  protected static final Long longValue = Long.MAX_VALUE;
  protected static final Short shortValue = Short.MAX_VALUE;
  protected static final String stringValue = "stringValue";

  private MinimalDatabaseServer databaseServer;

  private MemoryDatabaseManager databaseManager;

  protected OrientRecordStore store;

  protected RecordType RC_GOOD_EMPTY = new RecordType("GoodEmpty");

  protected RecordType RC_GOOD_SUPERTYPE = new RecordType("GoodSuperclass").withAbstract(true);

  protected RecordType RC_GOOD_SUBTYPE = new RecordType("GoodSubclass").withSuperType(RC_GOOD_SUPERTYPE);

  protected RecordType RC_GOOD_ALLFIELDTYPES = new RecordType("GoodAllFieldTypes")
      .withField(new FieldDefinition("bigDecimalField", BigDecimal.class))
      .withField(new FieldDefinition("byteField", Byte.class))
      .withField(new FieldDefinition("dateField", Date.class))
      .withField(new FieldDefinition("doubleField", Double.class))
      .withField(new FieldDefinition("floatField", Float.class))
      .withField(new FieldDefinition("integerField", Integer.class)
          .withNotNull(true)
          .withIndexed(true)
          .withUnique(true))
      .withField(new FieldDefinition("longField", Long.class))
      .withField(new FieldDefinition("shortField", Short.class))
      .withField(new FieldDefinition("stringField", String.class))
      .withField(new FieldDefinition("linkField", RecordId.class));

  protected RecordType RC_BAD_FIELDTYPE = new RecordType("BadFieldType")
      .withField(new FieldDefinition("fieldName", Object.class));


  @BeforeClass
  public static void setUpClass() {
    OLogManager.instance().setWarnEnabled(false);
  }

  @Before
  public void setUp() throws Exception {
    this.databaseServer = new MinimalDatabaseServer();
    databaseServer.start();

    this.databaseManager = new MemoryDatabaseManager();
    databaseManager.start();

    // initialize store
    store = new OrientRecordStore(databaseManager, new HexRecordIdObfuscator());
    store.start();

    // drop all test classes
    try (RecordStoreSession session = store.openSession()) {
      RecordStoreSchema schema = session.getSchema();
      // drop all non-built-in classes that have superclasses
      for (RecordType type : schema) {
        if (!isBuiltInClass(type.getName()) && type.getSuperType() != null) {
          schema.dropType(type.getName());
        }
      }
      // drop all remaining non-built-in classes
      for (RecordType type : schema) {
        if (!isBuiltInClass(type.getName())) {
          schema.dropType(type.getName());
        }
      }
    }
  }

  private static boolean isBuiltInClass(String name) {
    if (name.length() == 1) {
      return name.equals("V") || name.equals("E");
    }
    if (name.startsWith("O")) {
      String secondCharacter = name.substring(1, 1);
      return secondCharacter.toUpperCase().equals(secondCharacter);
    }
    return false;
  }

  @After
  public void tearDown() throws Exception {
    if (store != null) {
      store.stop();
      store = null;
    }

    if (databaseManager != null) {
      databaseManager.stop();
      databaseManager = null;
    }

    if (databaseServer != null) {
      databaseServer.stop();
      databaseServer = null;
    }
  }
}
