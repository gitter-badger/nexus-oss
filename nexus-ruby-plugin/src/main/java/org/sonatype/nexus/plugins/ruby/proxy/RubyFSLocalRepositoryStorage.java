package org.sonatype.nexus.plugins.ruby.proxy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.mime.MimeSupport;
import org.sonatype.nexus.plugins.ruby.NexusRubygemsFacade;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.LinkPersister;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;
import org.sonatype.nexus.proxy.storage.local.fs.FSPeer;
import org.sonatype.nexus.proxy.wastebasket.Wastebasket;
import org.sonatype.nexus.ruby.RubygemsFile;
import org.sonatype.nexus.ruby.cuba.DefaultRubygemsFileSystem;

@Singleton
@Named("rubyfile")
public class RubyFSLocalRepositoryStorage
    extends DefaultFSLocalRepositoryStorage
{

  private final NexusRubygemsFacade fileSystem = new NexusRubygemsFacade(new DefaultRubygemsFileSystem());

  @Inject
  public RubyFSLocalRepositoryStorage(Wastebasket wastebasket,
                                      LinkPersister linkPersister,
                                      MimeSupport mimeSupport,
                                      FSPeer fsPeer)
  {
    super(wastebasket, linkPersister, mimeSupport, fsPeer);
  }

  @Override
  public void storeItem(Repository repository, StorageItem item)
      throws UnsupportedStorageOperationException, LocalStorageException
  {
    if (!item.getPath().startsWith("/.nexus")) {

      RubygemsFile file = fileSystem.file(item.getResourceStoreRequest());

      switch (file.type()) {
        case NOT_FOUND:
          break;
        case BUNDLER_API:
          return;
        default:
          item.getResourceStoreRequest().setRequestPath(file.storagePath());
          ((AbstractStorageItem) item).setPath(file.storagePath());
      }
    }
    super.storeItem(repository, item);
  }
}
