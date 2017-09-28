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
package org.sonar.server.computation.task.projectanalysis.issue;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.core.issue.ShortBranchIssue;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.issue.ShortBranchIssueDto;
import org.sonar.server.computation.task.projectanalysis.component.Component;
import org.sonar.server.computation.task.projectanalysis.component.ShortBranchComponentsWithIssues;

public class ResolvedShortBranchIssuesLoader {

  private final ShortBranchComponentsWithIssues shortBranchComponentsWithIssues;
  private final DbClient dbClient;

  public ResolvedShortBranchIssuesLoader(ShortBranchComponentsWithIssues shortBranchComponentsWithIssues, DbClient dbClient) {
    this.shortBranchComponentsWithIssues = shortBranchComponentsWithIssues;
    this.dbClient = dbClient;
  }

  public Collection<ShortBranchIssue> create(Component component) {
    String componentKey = ComponentDto.removeBranchFromKey(component.getKey());
    Set<String> uuids = shortBranchComponentsWithIssues.getUuids(componentKey);
    if (uuids.isEmpty()) {
      return Collections.emptyList();
    }
    try (DbSession session = dbClient.openSession(false)) {
      return dbClient.issueDao().selectResolvedOrConfirmedByComponentUuids(session, uuids)
        .stream()
        .map(ShortBranchIssueDto::toShortBranchIssue)
        .collect(Collectors.toList());
    }
  }
}
