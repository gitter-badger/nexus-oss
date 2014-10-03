/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.nexus.ruby;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.sonatype.nexus.ruby.cuba.DefaultRubygemsFileSystem;
import org.sonatype.nexus.ruby.layout.CachingProxyStorage;
import org.sonatype.nexus.ruby.layout.HostedDELETELayout;
import org.sonatype.nexus.ruby.layout.HostedGETLayout;
import org.sonatype.nexus.ruby.layout.SimpleStorage;
import org.sonatype.nexus.ruby.layout.Storage;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(Parameterized.class)
public class HostedDELETELayoutTest
    extends TestSupport
{
  private static File proxyBase() throws IOException {
    File base = new File("target/proxy");
    FileUtils.deleteDirectory(base);
    return base;
  }

  private static File hostedBase() throws IOException {
    File source = new File("src/test/hostedrepo");
    File base = new File("target/repo");
    FileUtils.deleteDirectory(base);
    FileUtils.copyDirectory(source, base, true);
    return base;
  }

  @Parameters
  public static Collection<Object[]> stores() throws IOException {
    return Arrays.asList(new Object[][]{
        {new SimpleStorage(hostedBase())},
        {
            new CachingProxyStorage(proxyBase(), hostedBase().toURI().toURL())
            {

              protected URL toUrl(RubygemsFile file) throws MalformedURLException {
                return new URL(baseurl + file.storagePath());
              }
            }
        }
    });
  }

  private final DefaultRubygemsFileSystem fileSystem;

  public HostedDELETELayoutTest(Storage store) throws IOException {
    fileSystem = new DefaultRubygemsFileSystem(
        new HostedGETLayout(new DefaultRubygemsGateway(new TestScriptingContainer()),
            store),
        null,
        new HostedDELETELayout(new DefaultRubygemsGateway(new TestScriptingContainer()),
            new SimpleStorage(hostedBase())));
    // delete proxy files
    proxyBase();
  }

  @Before
  public void deleteGems() {
    fileSystem.delete("/gems/zip-2.0.2.gem");
  }

  @Test
  public void testSpecsZippedIndex() throws Exception {
    String[] pathes = {
        "/specs.4.8.gz",
        "/prerelease_specs.4.8.gz",
        "/latest_specs.4.8.gz"
    };
    assertFiletypeWithPayload(pathes, FileType.SPECS_INDEX_ZIPPED, InputStream.class);
  }

  @Test
  public void testSpecsUnzippedIndex() throws Exception {
    String[] pathes = {
        "/specs.4.8",
        "/prerelease_specs.4.8",
        "/latest_specs.4.8"
    };
    assertFiletypeWithPayload(pathes, FileType.SPECS_INDEX, GZIPInputStream.class);
  }

  @Test
  public void testSha1() throws Exception {
    String[] pathes = {
        "/maven/prereleases/rubygems/pre/0.1.0.beta-SNAPSHOT/pre-0.1.0.beta-123213123.gem.sha1",
        "/maven/prereleases/rubygems/pre/0.1.0.beta-SNAPSHOT/pre-0.1.0.beta-123213123.pom.sha1",
        "/maven/releases/rubygems/pre/0.1.0.beta/pre-0.1.0.beta.gem.sha1",
        "/maven/releases/rubygems/pre/0.1.0.beta/pre-0.1.0.beta.pom.sha1"
    };
    String[] shas = {
        "b7311d2f46398dbe40fd9643f3d4e5d473574335",
        "054121dcccc572cdee2da2d15e1ca712a1bb77b3",
        "b7311d2f46398dbe40fd9643f3d4e5d473574335",
        "a83efdc872c7b453196ec3911236f6e2dbd45c60"
    };

    assertFiletypeWithPayload(pathes, FileType.SHA1, shas);

    pathes = new String[]{
        "/maven/releases/rubygems/zip/2.0.2/zip-2.0.2.gem.sha1",
        "/maven/releases/rubygems/zip/2.0.2/zip-2.0.2.pom.sha1"
    };
    assertNotFound(pathes, FileType.SHA1);

    // these files carry a timestamp of creation of the json.rz file
    pathes = new String[]{
        "/maven/prereleases/rubygems/pre/maven-metadata.xml.sha1",
        "/maven/prereleases/rubygems/pre/0.1.0.beta-SNAPSHOT/maven-metadata.xml.sha1",
        "/maven/releases/rubygems/pre/maven-metadata.xml.sha1"
    };
    assertFiletypeWithPayload(pathes, FileType.SHA1, ByteArrayInputStream.class);
  }

  @Test
  public void testGemArtifact() throws Exception {
    String[] pathes = {
        "/maven/releases/rubygems/pre/0.1.0.beta/pre-0.1.0.beta.gem",
        "/maven/prereleases/rubygems/pre/0.1.0.beta-SNAPSHOT/pre-0.1.0.beta-123213123.gem"
    };
    assertFiletypeWithPayload(pathes, FileType.GEM_ARTIFACT, InputStream.class);

    pathes = new String[]{"/maven/releases/rubygems/zip/2.0.2/zip-2.0.2.gem"};
    assertNotFound(pathes, FileType.GEM_ARTIFACT);

    pathes = new String[]{
        "/maven/releases/rubygems/hufflepuf/0.1.0/hufflepuf-0.1.0.gem",
        "/maven/releases/rubygems/hufflepuf/0.1.0/hufflepuf-0.2.0.gem"
    };
    RubygemsFile[] result = assertFiletypeWithPayload(pathes, FileType.GEM_ARTIFACT, InputStream.class);
    for (RubygemsFile file : result) {
      GemArtifactFile a = (GemArtifactFile) file;
      assertThat(a.gem(null).filename(), is("hufflepuf-" + a.version() + "-universal-java-1.5"));
    }
  }

  @Test
  public void testPom() throws Exception {
    String[] pathes = {
        "/maven/releases/rubygems/pre/0.1.0.beta/jbundler-0.1.0.beta.pom",
        "/maven/prereleases/rubygems/pre/0.1.0.beta-SNAPSHOT/jbundler-0.1.0.beta-123213123.pom"
    };
    String[] xmls = {
        IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("pre.pom")),
        IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("pre-snapshot.pom"))
    };
    assertFiletypeWithPayload(pathes, FileType.POM, xmls);
    pathes = new String[]{"/maven/releases/rubygems/zip/2.0.2/zip-2.0.2.pom"};
    assertNotFound(pathes, FileType.POM);
  }

  @Test
  public void testMavenMetadata() throws Exception {
    String[] pathes = {
        "/maven/releases/rubygems/zip/maven-metadata.xml",
        "/maven/releases/rubygems/pre/maven-metadata.xml",
        "/maven/prereleases/rubygems/pre/maven-metadata.xml"
    };
    String[] xmls = {
        "<metadata>\n"
            + "  <groupId>rubygems</groupId>\n"
            + "  <artifactId>zip</artifactId>\n"
            + "  <versioning>\n"
            + "    <versions>\n"
            + "    </versions>\n"
            + "    <lastUpdated>2014</lastUpdated>\n"
            + "  </versioning>\n"
            + "</metadata>\n",

        "<metadata>\n"
            + "  <groupId>rubygems</groupId>\n"
            + "  <artifactId>pre</artifactId>\n"
            + "  <versioning>\n"
            + "    <versions>\n"
            + "    </versions>\n"
            + "    <lastUpdated>2014</lastUpdated>\n"
            + "  </versioning>\n"
            + "</metadata>\n",

        "<metadata>\n"
            + "  <groupId>rubygems</groupId>\n"
            + "  <artifactId>pre</artifactId>\n"
            + "  <versioning>\n"
            + "    <versions>\n"
            + "      <version>0.1.0.beta-SNAPSHOT</version>\n"
            + "    </versions>\n"
            + "    <lastUpdated>2014</lastUpdated>\n"
            + "  </versioning>\n"
            + "</metadata>\n"
    };
    assertFiletypeWithPayload(pathes, FileType.MAVEN_METADATA, xmls);
  }

  @Test
  public void testMavenMetadataSnapshot() throws Exception {
    String[] pathes = {"/maven/prereleases/rubygems/pre/0.1.0-SNAPSHOT/maven-metadata.xml"};
    String[] xmls = {
        "<metadata>\n"
            + "  <groupId>rubygems</groupId>\n"
            + "  <artifactId>pre</artifactId>\n"
            + "  <versioning>\n"
            + "    <versions>\n"
            + "      <snapshot>\n"
            + "        <timestamp>2014</timestamp>\n"
            + "        <buildNumber>1</buildNumber>\n"
            + "      </snapshot>\n"
            + "      <lastUpdated>2014</lastUpdated>\n"
            + "      <snapshotVersions>\n"
            + "        <snapshotVersion>\n"
            + "          <extension>gem</extension>\n"
            + "          <value>0.1.0-2014-1</value>\n"
            + "          <updated>2014</updated>\n"
            + "        </snapshotVersion>\n"
            + "        <snapshotVersion>\n"
            + "          <extension>pom</extension>\n"
            + "          <value>0.1.0-2014-1</value>\n"
            + "          <updated>2014</updated>\n"
            + "        </snapshotVersion>\n"
            + "      </snapshotVersions>\n"
            + "    </versions>\n"
            + "  </versioning>\n"
            + "</metadata>\n"
    };
    assertFiletypeWithPayload(pathes, FileType.MAVEN_METADATA_SNAPSHOT, xmls);
  }

  @Test
  public void testBundlerApi() throws Exception {
    String[] pathes = {"/api/v1/dependencies?gems=zip,pre"};
    assertFiletypeWithPayload(pathes, FileType.BUNDLER_API, org.sonatype.nexus.ruby.ByteArrayInputStream.class);
  }

  @Test
  public void testApiV1Gems() throws Exception {
    String[] pathes = {"/api/v1/gems"};
    assertForbidden(pathes);
  }

  @Test
  public void testApiV1ApiKey() throws Exception {
    String[] pathes = {"/api/v1/api_key"};
    assertFiletypeWithNullPayload(pathes, FileType.API_V1);
  }

  @Test
  public void testDependency() throws Exception {
    String[] pathes = {
        "/api/v1/dependencies?gems=zip", "/api/v1/dependencies/pre.json.rz", "/api/v1/dependencies/z/zip.json.rz"
    };
    assertFiletypeWithPayload(pathes, FileType.DEPENDENCY, InputStream.class);
  }

  @Test
  public void testGemspec() throws Exception {
    String[] pathes = {
        "/quick/Marshal.4.8/pre-0.1.0.beta.gemspec.rz", "/quick/Marshal.4.8/p/pre-0.1.0.beta.gemspec.rz"
    };
    assertFiletypeWithPayload(pathes, FileType.GEMSPEC, InputStream.class);
    pathes = new String[]{"/quick/Marshal.4.8/zip-2.0.2.gemspec.rz", "/quick/Marshal.4.8/z/zip-2.0.2.gemspec.rz"};
    assertNotFound(pathes, FileType.GEMSPEC);
  }

  @Test
  public void testGem() throws Exception {
    String[] pathes = {"/gems/pre-0.1.0.beta.gem", "/gems/p/pre-0.1.0.beta.gem"};
    assertFiletypeWithPayload(pathes, FileType.GEM, InputStream.class);
    pathes = new String[]{"/gems/zip-2.0.2.gem", "/gems/zip-2.0.2.gem"};
    assertNotFound(pathes, FileType.GEM);
  }

  @Test
  public void testDirectory() throws Exception {
    String[] pathes = {
        "/", "/api", "/api/", "/api/v1", "/api/v1/",
        "/api/v1/dependencies", "/gems/", "/gems",
        "/maven/releases/rubygems/jbundler",
        "/maven/releases/rubygems/jbundler/1.2.3",
        "/maven/prereleases/rubygems/jbundler",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT",
    };
    assertFiletypeWithNullPayload(pathes, FileType.DIRECTORY);
  }

  @Test
  public void testNotFound() throws Exception {
    String[] pathes = {
        "/asa", "/asa/", "/api/a", "/api/v1ds", "/api/v1/ds",
        "/api/v1/dependencies/jbundler.jsaon.rz", "/api/v1/dependencies/b/bundler.json.rzd",
        "/api/v1/dependencies/basd/bundler.json.rz",
        "/quick/Marshal.4.8/jbundler.jssaon.rz", "/quick/Marshal.4.8/b/bundler.gemspec.rzd",
        "/quick/Marshal.4.8/basd/bundler.gemspec.rz",
        "/gems/jbundler.jssaonrz", "/gems/b/bundler.gemsa",
        "/gems/basd/bundler.gem",
        "/maven/releasesss/rubygemsss/a",
        "/maven/releases/rubygemsss/jbundler",
        "/maven/releases/rubygems/jbundler/1.2.3/jbundler-1.2.3.gema",
        "/maven/releases/rubygems/jbundler/1.2.3/jbundler-1.2.3.pom2",
        "/maven/releases/rubygems/jbundler/1.2.3/jbundler-1.2.3.gem.sha",
        "/maven/releases/rubygems/jbundler/1.2.3/jbundler-1.2.3.pom.msa",
        "/maven/prereleases/rubygemsss/jbundler",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT/maven-metadata.xml.sha1a",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT/jbundler-1.2.3-123213123.gem.sh1",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT/jbundler-1.2.3-123213123.pom.sha",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT/jbundler-1.2.3-123213123.gema",
        "/maven/prereleases/rubygems/jbundler/1.2.3-SNAPSHOT/jbundler-1.2.3-123213123.pom2",
    };
    assertFiletypeWithNullPayload(pathes, FileType.NOT_FOUND, false);
  }

  protected void assertFiletype(String[] pathes, FileType type) {
    for (String path : pathes) {
      RubygemsFile file = fileSystem.get(path);
      assertThat(path, file.type(), equalTo(type));
      assertThat(path, file.get(), notNullValue());
      assertThat(path, file.hasException(), is(false));
    }
  }

  protected void assertFiletypeWithPayload(String[] pathes, FileType type, String[] payloads) {
    int index = 0;
    for (String path : pathes) {
      RubygemsFile file = fileSystem.get(path);
      assertThat(path, file.type(), equalTo(type));
      assertThat(path, file.get(), is(instanceOf(ByteArrayInputStream.class)));
      assertThat(path, file.hasException(), is(false));
      assertThat(path, readPayload(file).replaceAll("[0-9]{8}\\.?[0-9]{6}", "2014"), equalTo(payloads[index++]));
    }
  }

  protected String readPayload(RubygemsFile file) {
    ByteArrayInputStream b = (ByteArrayInputStream) file.get();
    byte[] bb = new byte[b.available()];
    try {
      b.read(bb);
    }
    catch (IOException e) {
      new RuntimeException(e);
    }
    return new String(bb);
  }

  protected RubygemsFile[] assertFiletypeWithPayload(String[] pathes, FileType type, Class<?> payload) {
    RubygemsFile[] result = new RubygemsFile[pathes.length];
    int index = 0;
    for (String path : pathes) {
      RubygemsFile file = fileSystem.get(path);
      assertThat(path, file.type(), equalTo(type));
      assertThat(path, file.get(), is(instanceOf(payload)));
      assertThat(path, file.hasException(), is(false));
      result[index++] = file;
    }
    return result;
  }

  protected void assertFiletypeWithNullPayload(String[] pathes, FileType type) {
    assertFiletypeWithNullPayload(pathes, type, true);
  }

  protected void assertFiletypeWithNullPayload(String[] pathes, FileType type, boolean found) {
    for (String path : pathes) {
      RubygemsFile file = fileSystem.get(path);
      assertThat(path, file.type(), equalTo(type));
      assertThat(path, file.get(), nullValue());
      assertThat(path, file.hasNoPayload(), is(found));
      assertThat(path, file.hasException(), is(false));
      assertThat(path, file.exists(), is(found));
    }
  }

  protected void assertNotFound(String[] pathes, FileType type) {
    assertFiletypeWithNullPayload(pathes, type, false);
  }

  //
  //    protected void assertNotFound( String[] pathes, FileType type )
  //    {
  //        for( String path : pathes )
  //        {
  //            RubygemsFile file = bootstrap.accept( path );
  //            assertThat( path, file.type(), equalTo( type ) );
  //            assertThat( path, file.exists(), is( false ) );
  //        }
  //    }
  //    protected void assertIOException( String[] pathes, FileType type )
  //    {
  //        for( String path : pathes )
  //        {
  //            RubygemsFile file = bootstrap.accept( path );
  //            assertThat( path, file.type(), equalTo( type ) );
  //            assertThat( path, file.get(), nullValue() );
  //            assertThat( path, file.getException(), is( instanceOf( IOException.class ) ) );
  //        }
  //    }

  protected void assertForbidden(String[] pathes) {
    for (String path : pathes) {
      assertThat(path, fileSystem.get(path).forbidden(), is(true));
    }
  }
}