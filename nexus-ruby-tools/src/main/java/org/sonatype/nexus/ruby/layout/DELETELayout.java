package org.sonatype.nexus.ruby.layout;

import org.sonatype.nexus.ruby.ApiV1File;
import org.sonatype.nexus.ruby.DependencyFile;
import org.sonatype.nexus.ruby.GemFile;
import org.sonatype.nexus.ruby.GemspecFile;
import org.sonatype.nexus.ruby.RubygemsGateway;
import org.sonatype.nexus.ruby.SpecsIndexFile;
import org.sonatype.nexus.ruby.SpecsIndexZippedFile;

public class DELETELayout extends NoopDefaultLayout
{
    public DELETELayout( RubygemsGateway gateway, Storage store )
    {
        super( gateway, store );
    }

    @Override
    public SpecsIndexFile specsIndexFile( String name )
    {
        return null;
    }

    @Override
    public SpecsIndexZippedFile specsIndexZippedFile( String name )
    {
        SpecsIndexZippedFile file = super.specsIndexZippedFile( name );
        store.delete( file );
        return file;
    }
    
    @Override
    public ApiV1File apiV1File( String name )
    {
        return null;
    }

    @Override
    public GemFile gemFile( String name, String version, String platform )
    {
        GemFile file = super.gemFile( name, version, platform );
        store.delete( file );
        return file;
    }

    @Override
    public GemFile gemFile( String name )
    {
        GemFile file = super.gemFile( name );
        store.delete( file );
        return file;
    }

    @Override
    public GemspecFile gemspecFile( String name, String version, String platform )
    {
        GemspecFile file = super.gemspecFile( name, version, platform );
        store.delete( file );
        return file;
    }

    @Override
    public GemspecFile gemspecFile( String name )
    {
        GemspecFile file = super.gemspecFile( name );
        store.delete( file );
        return file;
    }

    @Override
    public DependencyFile dependencyFile( String name )
    {
        DependencyFile file = super.dependencyFile( name );
        store.delete( file );
        return file;
    }   
}