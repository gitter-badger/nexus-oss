package org.sonatype.nexus.plugins.ruby;

import org.sonatype.nexus.plugins.ruby.fs.RubygemsFacade;
import org.sonatype.nexus.proxy.AccessDeniedException;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.RemoteAccessException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.StorageException;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;

public interface RubyRepository
    extends Repository
{

    RubygemsFacade getRubygemsFacade();
    
    public StorageItem superRetrieveItem( ResourceStoreRequest request ) 
            throws AccessDeniedException, IllegalOperationException, 
            ItemNotFoundException, RemoteAccessException, 
            StorageException;
    
    @SuppressWarnings("deprecation")
    StorageFileItem retrieveGemspec( String name ) 
            throws AccessDeniedException, IllegalOperationException, 
            org.sonatype.nexus.proxy.StorageException, 
            ItemNotFoundException;

    void storeDependencies( String gemname, String json )
            throws LocalStorageException, UnsupportedStorageOperationException;

    StorageFileItem retrieveDependenciesItem( String gemname )
            throws LocalStorageException, ItemNotFoundException;
}
