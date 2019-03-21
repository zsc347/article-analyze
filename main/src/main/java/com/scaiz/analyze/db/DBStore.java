package com.scaiz.analyze.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBStore {

  private static final String DB_NAME = "article.db";
  private static final int CONN_POOL_SIZE = 2;
  private static LinkedBlockingQueue<Connection> pool = new LinkedBlockingQueue<>(2);

  public static final String HISTORY_TABLE_NAME = "history";

  public Connection getConnection() {
    return pool.peek();
  }

  static {
    prepareConnPool();
    prepareTable();
  }

  private static void prepareConnPool() {
    try {
      Class.forName("org.sqlite.JDBC");
      for (int i = 0; i < CONN_POOL_SIZE; i++) {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
        conn.setAutoCommit(false);
        pool.offer(conn);
      }
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
  }

  private static void prepareTable() {
      Connection conn = DBStore.instance().getConnection();
      try {
        Statement stmt = conn.createStatement();
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s " +
            "(id INT PRIMARY KEY     NOT NULL," +
            " title           TEXT    NOT NULL, " +
            " query           TEXT     NOT NULL, " +
            " filtered        TEXT NOT NULL)", HISTORY_TABLE_NAME);
        stmt.executeUpdate(sql);
        stmt.close();
        conn.commit();
        log.info("First time to use, create table complete.");
      } catch (SQLException e) {
        throw new Error("Failed to prepare sqlite db table");
      }
  }

  public void shutdown() {
    try {
      for(Connection conn: pool) {
        conn.close();
      }
    } catch (Exception ignore) {
    }
  }

  public static class DBStoreHolder {
    private static DBStore instance = new DBStore();
  }

  public static DBStore instance() {
    return DBStoreHolder.instance;
  }
}
