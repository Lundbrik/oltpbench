package com.oltpbenchmark.benchmarks.eve;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Random;

import com.oltpbenchmark.api.Loader;
import com.oltpbenchmark.catalog.Table;
import com.oltpbenchmark.util.SQLUtil;
import com.oltpbenchmark.types.DatabaseType;

public class EveLoader extends Loader {

  private static final String[] classes = new String[] {
    "Destroyer",
    "Battlecruiser",
    "Viking",
    "Banshee"};

  public EveLoader(EveBenchmark benchmark, Connection conn) {
    super(benchmark, conn);
  }

  @Override
  public void load() throws SQLException {

    Random rand = new Random();
    int numShips = ((EveBenchmark)this.benchmark).numShips;
    int reach_min = EveConstants.REACH_MIN;
    int reach_max = EveConstants.REACH_MAX;
    int ss_max = EveConstants.SS_MAX;
    int ss_min = EveConstants.SS_MIN;

    Table tbl = getTableCatalog(EveConstants.TABLENAME_CLASSES);
    String sql = null;
    if(this.getDatabaseType() == DatabaseType.POSTGRES )
      sql = SQLUtil.getInsertSQL(tbl, false);
    else
      sql = SQLUtil.getInsertSQL(tbl);

    PreparedStatement ps = this.conn.prepareStatement(sql);
    for (int i = 0; i < 4; i++) {
      ps.setInt(1, i + 1);
      ps.setString(2, classes[i]);
      ps.setInt(3, rand.nextInt((reach_max - reach_min) + 1) + reach_min);
      ps.addBatch();
    }
    ps.executeBatch();

    tbl = getTableCatalog(EveConstants.TABLENAME_SOLARSYSTEMS);
    if(this.getDatabaseType() == DatabaseType.POSTGRES )
      sql = SQLUtil.getInsertSQL(tbl, false);
    else
      sql = SQLUtil.getInsertSQL(tbl);
    ps = this.conn.prepareStatement(sql);
    // only one solarsystem add loop for more
    ps.setInt(1, 100); //id
    ps.setInt(2, rand.nextInt((ss_max - ss_min) + 1) + ss_min);
    ps.setInt(3, rand.nextInt((ss_max - ss_min) + 1) + ss_min);
    ps.addBatch();
    ps.executeBatch();

    tbl = getTableCatalog(EveConstants.TABLENAME_SHIPS);
    if(this.getDatabaseType() == DatabaseType.POSTGRES )
      sql = SQLUtil.getInsertSQL(tbl, false);
    else
      sql = SQLUtil.getInsertSQL(tbl);
    ps = this.conn.prepareStatement(sql);
    for (int i = 0; i < numShips; i++) {
      ps.setInt(1, i + 1);
      ps.setInt(2, i); //fix
      ps.setInt(3, i); //fix
      ps.setInt(4, (i % 4) + 1);
      ps.setInt(5, 100);
      ps.addBatch();
    }
    ps.executeBatch();
  }
}
