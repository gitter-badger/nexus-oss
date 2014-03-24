package org.sonatype.nexus.ruby;

public class ApiV1File extends RubygemsFile {
    
    ApiV1File( FileLayout layout, String remote, String name )
    {
        super( layout, FileType.API_V1, remote, remote, name );
    }
}