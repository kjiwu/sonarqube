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
package org.sonar.application.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.sonar.application.es.EsFileSystem;
import org.sonar.application.es.EsYmlSettings;
import org.sonar.process.ProcessId;
import org.sonar.process.System2;

public class EsCommand extends AbstractCommand<EsCommand> {
  private EsFileSystem fileSystem;
  private String clusterName;
  private String host;
  private int port;
  private Properties log4j2Properties;
  private List<String> esOptions = new ArrayList<>();
  private EsJvmOptions esJvmOptions;
  private EsYmlSettings esYmlSettings;

  public EsCommand(ProcessId id, File workDir) {
    super(id, workDir, System2.INSTANCE);
  }

  public EsFileSystem getFileSystem() {
    return fileSystem;
  }

  public EsCommand setFileSystem(EsFileSystem fileSystem) {
    this.fileSystem = fileSystem;
    return this;
  }

  public String getClusterName() {
    return clusterName;
  }

  public EsCommand setClusterName(String clusterName) {
    this.clusterName = clusterName;
    return this;
  }

  public String getHost() {
    return host;
  }

  public EsCommand setHost(String host) {
    this.host = host;
    return this;
  }

  public int getPort() {
    return port;
  }

  public EsCommand setPort(int port) {
    this.port = port;
    return this;
  }

  public Properties getLog4j2Properties() {
    return log4j2Properties;
  }

  public EsCommand setLog4j2Properties(Properties log4j2Properties) {
    this.log4j2Properties = log4j2Properties;
    return this;
  }

  public List<String> getEsOptions() {
    return esOptions;
  }

  public EsCommand addEsOption(String s) {
    if (!s.isEmpty()) {
      esOptions.add(s);
    }
    return this;
  }

  public EsCommand setEsJvmOptions(EsJvmOptions esJvmOptions) {
    this.esJvmOptions = esJvmOptions;
    return this;
  }

  public EsJvmOptions getEsJvmOptions() {
    return esJvmOptions;
  }

  public EsCommand setEsYmlSettings(EsYmlSettings esYmlSettings) {
    this.esYmlSettings = esYmlSettings;
    return this;
  }

  public EsYmlSettings getEsYmlSettings() {
    return esYmlSettings;
  }
}
