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

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.rule.RuleKey;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.ShortBranchIssue;
import org.sonar.core.issue.tracking.SimpleTracker;
import org.sonar.server.computation.task.projectanalysis.component.Component;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class IssueStatusCopierTest {
  @Mock
  private ResolvedShortBranchIssuesLoader resolvedShortBranchIssuesLoader;
  @Mock
  private IssueLifecycle issueLifecycle;
  @Mock
  private Component component;

  private SimpleTracker<DefaultIssue, ShortBranchIssue> tracker = new SimpleTracker<>();
  private IssueStatusCopier copier;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    copier = new IssueStatusCopier(resolvedShortBranchIssuesLoader, tracker, issueLifecycle);
  }

  @Test
  public void do_nothing_if_no_match() {
    when(resolvedShortBranchIssuesLoader.create(component)).thenReturn(Collections.emptyList());
    DefaultIssue i = createIssue("issue1", "rule1");
    copier.updateStatus(component, Collections.singleton(i));

    verify(resolvedShortBranchIssuesLoader).create(component);
    verifyZeroInteractions(issueLifecycle);
  }

  @Test
  public void do_nothing_if_no_new_issue() {
    DefaultIssue i = createIssue("issue1", "rule1");
    when(resolvedShortBranchIssuesLoader.create(component)).thenReturn(Collections.singleton(newShortBranchIssue(i)));
    copier.updateStatus(component, Collections.emptyList());

    verify(resolvedShortBranchIssuesLoader).create(component);
    verifyZeroInteractions(issueLifecycle);
  }

  @Test
  public void update_status_on_matches() {
    ShortBranchIssue shortBranchIssue = newShortBranchIssue(createIssue("issue1", "rule1"));
    DefaultIssue newIssue = createIssue("issue2", "rule1");

    when(resolvedShortBranchIssuesLoader.create(component)).thenReturn(Collections.singleton(shortBranchIssue));
    copier.updateStatus(component, Collections.singleton(newIssue));
    verify(issueLifecycle).copyResolution(newIssue, shortBranchIssue.getStatus(), shortBranchIssue.getResolution());
  }

  private static DefaultIssue createIssue(String key, String ruleKey) {
    DefaultIssue issue = new DefaultIssue();
    issue.setKey(key);
    issue.setRuleKey(RuleKey.of("repo", ruleKey));
    issue.setMessage("msg");
    issue.setLine(1);
    return issue;
  }

  private ShortBranchIssue newShortBranchIssue(DefaultIssue i) {
    return new ShortBranchIssue(i.line(), i.message(), i.getLineHash(), i.ruleKey(), i.status(), i.resolution());
  }
}
