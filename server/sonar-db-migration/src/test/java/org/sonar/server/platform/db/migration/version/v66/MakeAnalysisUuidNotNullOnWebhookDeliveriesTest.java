package org.sonar.server.platform.db.migration.version.v66;/*
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

import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;

import static org.assertj.core.api.Assertions.assertThat;

public class MakeAnalysisUuidNotNullOnWebhookDeliveriesTest {
  private static final String TABLE = "webhook_deliveries";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Rule
  public final CoreDbTester db = CoreDbTester.createForSchema(MakeAnalysisUuidNotNullOnWebhookDeliveriesTest.class, "initial.sql");

  private MakeAnalysisUuidNotNullOnWebhookDeliveries underTest = new MakeAnalysisUuidNotNullOnWebhookDeliveries(db.database());
  @Test
  public void update_column() throws SQLException {
    underTest.execute();

    assertThat(db.countRowsOfTable(TABLE)).isEqualTo(0);
    db.assertColumnDefinition(TABLE, "analysis_uuid", Types.VARCHAR, 40, false);
    db.assertIndex(TABLE, "analysis_uuid", "analysis_uuid");
  }

  @Test
  public void migration_is_not_reentrant() throws SQLException {
    underTest.execute();

    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Fail to execute CREATE INDEX analysis_uuid ON webhook_deliveries (analysis_uuid)");

    underTest.execute();
  }
}
