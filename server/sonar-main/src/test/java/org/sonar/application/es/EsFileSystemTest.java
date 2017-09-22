/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application.es;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.process.ProcessProperties;
import org.sonar.process.Props;

import static org.assertj.core.api.Assertions.assertThat;

public class EsFileSystemTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void constructor_fails_with_IAE_if_sq_home_property_is_not_defined() {
    Props props = new Props(new Properties());

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property sonar.path.home is not set");

    new EsFileSystem(props);
  }

  @Test
  public void constructor_fails_with_IAE_if_temp_dir_property_is_not_defined() throws IOException {
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property sonar.path.temp is not set");

    new EsFileSystem(props);
  }

  @Test
  public void constructor_fails_with_IAE_if_data_dir_property_is_not_defined() throws IOException {
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Missing property: sonar.path.data");

    new EsFileSystem(props);
  }

  @Test
  public void getHomeDirectory_is_elasticsearch_subdirectory_of_sq_home_directory() throws IOException {
    File sqHomeDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, sqHomeDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getHomeDirectory()).isEqualTo(new File(sqHomeDir, "elasticsearch"));
  }

  @Test
  public void override_data_dir() throws Exception {
    File sqHomeDir = temp.newFolder();
    File tempDir = temp.newFolder();
    File dataDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_HOME, sqHomeDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, tempDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    props.set(ProcessProperties.PATH_DATA, dataDir.getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getDataDirectory()).isEqualTo(new File(dataDir, "es5"));
  }

  @Test
  public void getLogDirectory_is_configured_with_non_nullable_PATH_LOG_variable() throws IOException {
    File sqHomeDir = temp.newFolder();
    File logDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, sqHomeDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, logDir.getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getLogDirectory()).isEqualTo(logDir);
  }

  @Test
  public void conf_directory_is_conf_es_subdirectory_of_sq_temp_directory() throws IOException {
    File tempDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, tempDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getConfDirectory()).isEqualTo(new File(tempDir, "conf/es"));
  }

  @Test
  public void getExecutable_resolve_executable_for_platform() throws IOException {
    File sqHomeDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, sqHomeDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    if (System.getProperty("os.name").startsWith("Windows")) {
      assertThat(underTest.getExecutable()).isEqualTo(new File(sqHomeDir, "elasticsearch/bin/elasticsearch.bat"));
    } else {
      assertThat(underTest.getExecutable()).isEqualTo(new File(sqHomeDir, "elasticsearch/bin/elasticsearch"));
    }
  }

  @Test
  public void getLog4j2Properties_is_in_es_conf_directory() throws IOException {
    File tempDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, tempDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getLog4j2Properties()).isEqualTo(new File(tempDir, "conf/es/log4j2.properties"));
  }

  @Test
  public void getElasticsearchYml_is_in_es_conf_directory() throws IOException {
    File tempDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, tempDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getElasticsearchYml()).isEqualTo(new File(tempDir, "conf/es/elasticsearch.yml"));
  }

  @Test
  public void getJvmOptions_is_in_es_conf_directory() throws IOException {
    File tempDir = temp.newFolder();
    Props props = new Props(new Properties());
    props.set(ProcessProperties.PATH_DATA, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_HOME, temp.newFolder().getAbsolutePath());
    props.set(ProcessProperties.PATH_TEMP, tempDir.getAbsolutePath());
    props.set(ProcessProperties.PATH_LOGS, temp.newFolder().getAbsolutePath());

    EsFileSystem underTest = new EsFileSystem(props);

    assertThat(underTest.getJvmOptions()).isEqualTo(new File(tempDir, "conf/es/jvm.options"));
  }
}
