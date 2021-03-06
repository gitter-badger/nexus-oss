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

package org.sonatype.nexus.timeline.feeds.subscribers;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.events.Asynchronous;
import org.sonatype.nexus.events.Event;
import org.sonatype.nexus.events.EventSubscriber;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.proxy.events.RepositoryItemEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCache;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCacheCreate;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCacheUpdate;
import org.sonatype.nexus.proxy.events.RepositoryItemEventDelete;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStoreCreate;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStoreUpdate;
import org.sonatype.nexus.proxy.events.RepositoryItemValidationEventFailed;
import org.sonatype.nexus.proxy.events.RepositoryItemValidationEventFailedChecksum;
import org.sonatype.nexus.proxy.events.RepositoryItemValidationEventFailedFileType;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.uid.IsHiddenAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenArtifactSignatureAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenChecksumAttribute;
import org.sonatype.nexus.proxy.maven.uid.IsMavenRepositoryMetadataAttribute;
import org.sonatype.nexus.timeline.feeds.FeedEvent;
import org.sonatype.nexus.timeline.feeds.FeedRecorder;

import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * Subscriber listening for events recorded under {@link FeedRecorder#FAMILY_ITEM} event type.
 */
@Named
@Singleton
public class ItemSubscriber
    extends AbstractFeedEventSubscriber
    implements EventSubscriber, Asynchronous
{
  @Inject
  public ItemSubscriber(final FeedRecorder feedRecorder) {
    super(feedRecorder);
  }

  // RETRIEVE event creates a lot of noise in events, so we are not processing those

  @Subscribe
  @AllowConcurrentEvents
  public void on(final RepositoryItemEventCache evt) {
    inspectForNexus(evt);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void on(final RepositoryItemEventStore evt) {
    inspectForNexus(evt);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void on(final RepositoryItemEventDelete evt) {
    inspectForNexus(evt);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void on(final RepositoryItemValidationEventFailed ievt) {
    if (ievt.getItem() instanceof StorageFileItem) {
      String action = FeedRecorder.ITEM_BROKEN;
      if (ievt instanceof RepositoryItemValidationEventFailedChecksum) {
        action = FeedRecorder.ITEM_BROKEN_WRONG_REMOTE_CHECKSUM;
      }
      else if (ievt instanceof RepositoryItemValidationEventFailedFileType) {
        action = FeedRecorder.ITEM_BROKEN_INVALID_CONTENT;
      }

      final StorageFileItem fileItem = (StorageFileItem) ievt.getItem();
      final Map<String, String> data = Maps.newHashMap();
      putIfNotNull(data, "validationMessage", ievt.getMessage());
      putIfNotNull(data, "repoId", fileItem.getRepositoryItemUid().getRepository().getId());
      putIfNotNull(data, "repoName", fileItem.getRepositoryItemUid().getRepository().getName());
      putIfNotNull(data, "itemPath", fileItem.getPath());
      putIfNotNull(data, "itemRemoteUrl", fileItem.getRemoteUrl());
      final String userId = (String) fileItem.getResourceStoreRequest().getRequestContext()
          .get(AccessManager.REQUEST_USER);
      putIfNotNull(data, "userId", userId);
      putIfNotNull(data, "userIp",
          (String) fileItem.getResourceStoreRequest().getRequestContext().get(AccessManager.REQUEST_REMOTE_ADDRESS));
      putIfNotNull(data, "userUa",
          (String) fileItem.getResourceStoreRequest().getRequestContext().get(AccessManager.REQUEST_AGENT));
      final FeedEvent fe = new FeedEvent(
          FeedRecorder.FAMILY_ITEM,
          action,
          ievt.getEventDate(),
          userId,
          "/content/repositories/" + fileItem.getRepositoryId() + fileItem.getPath(), // link to item
          data
      );
      getFeedRecorder().addEvent(fe);
    }
  }

  private void inspectForNexus(Event<?> evt) {
    RepositoryItemEvent ievt = (RepositoryItemEvent) evt;

    // filter out links and dirs/collections and hidden files
    if (StorageFileItem.class.isAssignableFrom(ievt.getItem().getClass())
        && !ievt.getItemUid().getBooleanAttributeValue(IsHiddenAttribute.class)
        && !ievt.getItemUid().getBooleanAttributeValue(IsMavenRepositoryMetadataAttribute.class) // "maven-metadata.xml"
        && !ievt.getItemUid().getBooleanAttributeValue(IsMavenArtifactSignatureAttribute.class) // "*.asc"
        && !ievt.getItemUid().getBooleanAttributeValue(IsMavenChecksumAttribute.class) // "*.sha1" or "*.md5"
        && !((StorageFileItem) ievt.getItem()).isContentGenerated()) {

      String action;
      if (ievt instanceof RepositoryItemEventCacheCreate) {
        action = FeedRecorder.ITEM_CACHED;
      }
      else if (ievt instanceof RepositoryItemEventCacheUpdate) {
        action = FeedRecorder.ITEM_CACHED_UPDATE;
      }
      else if (ievt instanceof RepositoryItemEventStoreCreate) {
        action = FeedRecorder.ITEM_DEPLOYED;
      }
      else if (ievt instanceof RepositoryItemEventStoreUpdate) {
        action = FeedRecorder.ITEM_DEPLOYED_UPDATE;
      }
      else if (ievt instanceof RepositoryItemEventDelete) {
        action = FeedRecorder.ITEM_DELETED;
      }
      else {
        return;
      }

      final StorageFileItem fileItem = (StorageFileItem) ievt.getItem();
      final Map<String, String> data = Maps.newHashMap();
      putIfNotNull(data, "repoId", fileItem.getRepositoryItemUid().getRepository().getId());
      putIfNotNull(data, "repoName", fileItem.getRepositoryItemUid().getRepository().getName());
      putIfNotNull(data, "itemPath", fileItem.getPath());
      putIfNotNull(data, "itemRemoteUrl", fileItem.getRemoteUrl());
      final String userId = (String) fileItem.getResourceStoreRequest().getRequestContext()
          .get(AccessManager.REQUEST_USER);
      putIfNotNull(data, "userId", userId);
      putIfNotNull(data, "userIp",
          (String) fileItem.getResourceStoreRequest().getRequestContext().get(AccessManager.REQUEST_REMOTE_ADDRESS));
      putIfNotNull(data, "userUa",
          (String) fileItem.getResourceStoreRequest().getRequestContext().get(AccessManager.REQUEST_AGENT));
      final FeedEvent fe = new FeedEvent(
          FeedRecorder.FAMILY_ITEM,
          action,
          evt.getEventDate(),
          userId,
          "/content/repositories/" + fileItem.getRepositoryId() + fileItem.getPath(), // link to item
          data
      );
      getFeedRecorder().addEvent(fe);
    }
  }

}
