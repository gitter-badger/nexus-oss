package org.sonatype.nexus.ruby.layout;

import org.sonatype.nexus.ruby.ApiV1File;
import org.sonatype.nexus.ruby.DependencyFile;
import org.sonatype.nexus.ruby.GemFile;
import org.sonatype.nexus.ruby.GemspecFile;
import org.sonatype.nexus.ruby.RubygemsGateway;
import org.sonatype.nexus.ruby.SpecsIndexFile;
import org.sonatype.nexus.ruby.SpecsIndexZippedFile;

/**
 * layout for HTTP DELETE request. allows to delete
 * <li><code>SpecsIndexZippedFile</code></li>
 * <li><code>GemFile</code></li>
 * <li><code>GemspecFile</code></li>
 * <li><code>DependencyFile</code></li>
 *
 * and disallows
 * <li><code>SpecsIndexFile</code></li>
 * <li><code>ApiV1File</code></li>
 *
 * @author christian
 */
public class DELETELayout
    extends NoopDefaultLayout
{
  public DELETELayout(RubygemsGateway gateway, Storage store) {
    super(gateway, store);
  }

  @Override
  public SpecsIndexFile specsIndexFile(String name) {
    SpecsIndexFile file = super.specsIndexFile(name);
    file.markAsForbidden();
    return file;
  }

  @Override
  public SpecsIndexZippedFile specsIndexZippedFile(String name) {
    SpecsIndexZippedFile file = super.specsIndexZippedFile(name);
    store.delete(file);
    return file;
  }

  @Override
  public ApiV1File apiV1File(String name) {
    ApiV1File file = super.apiV1File(name);
    file.markAsForbidden();
    return file;
  }

  @Override
  public GemFile gemFile(String name, String version, String platform) {
    GemFile file = super.gemFile(name, version, platform);
    store.delete(file);
    return file;
  }

  @Override
  public GemFile gemFile(String name) {
    GemFile file = super.gemFile(name);
    store.delete(file);
    return file;
  }

  @Override
  public GemspecFile gemspecFile(String name, String version, String platform) {
    GemspecFile file = super.gemspecFile(name, version, platform);
    store.delete(file);
    return file;
  }

  @Override
  public GemspecFile gemspecFile(String name) {
    GemspecFile file = super.gemspecFile(name);
    store.delete(file);
    return file;
  }

  @Override
  public DependencyFile dependencyFile(String name) {
    DependencyFile file = super.dependencyFile(name);
    store.delete(file);
    return file;
  }
}