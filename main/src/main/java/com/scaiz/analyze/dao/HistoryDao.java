package com.scaiz.analyze.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scaiz.analyze.db.DBStore;
import com.scaiz.analyze.pojo.History;
import io.vertx.core.json.Json;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryDao {

  // TODO prepare statement to prevent sql injection

  public void save(History history) {
    String insertFmt = "INSERT INTO %s (id , title, query, filtered) " +
        "VALUES (%d, '%s', '%s', '%s' );";
    String sql = String.format(insertFmt, DBStore.HISTORY_TABLE_NAME,
        history.getEpochTime(),
        history.getTitle(),
        history.getQuery(),
        Json.encode(history.getFiltered()));

    System.out.println(sql);
    try {
      Connection conn = DBStore.instance().getConnection();
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(sql);
      System.out.println(sql);
      stmt.close();
      conn.commit();
    } catch (Exception e) {
      log.error("fail", e);
      throw new UncheckedIOException(new IOException("Failed to write data to db"));
    }
  }

  public boolean remove(long id) {
    String deleteFmt = "DELETE FROM %s where id = %d ";

    String sql = String.format(deleteFmt, DBStore.HISTORY_TABLE_NAME, id);

    System.out.println(sql);
    try {
      Connection conn = DBStore.instance().getConnection();
      Statement stmt = conn.createStatement();
      int count = stmt.executeUpdate(sql);
      stmt.close();
      conn.commit();
      return count > 0;
    } catch (Exception e) {
      throw new UncheckedIOException(new IOException("Failed to delete item", e));
    }
  }

  public List<History> loadAll() {
    String deleteFmt = "SELECT * FROM %s";
    String sql = String.format(deleteFmt, DBStore.HISTORY_TABLE_NAME);
    try {
      Connection conn = DBStore.instance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);

      TypeReference<List<Integer>> type = new TypeReference<List<Integer>>() {
      };

      List<History> hiss = new LinkedList<>();
      while (rs.next()) {
          History history = new History();
          history.setEpochTime(rs.getLong("id"));
          history.setTitle(rs.getString("title"));
          history.setQuery(rs.getString("query"));
          history.setFiltered(Json.decodeValue(rs.getString("filtered"), type));
        hiss.add(history);
      }
      return hiss;
    } catch (Exception e) {
      throw new UncheckedIOException(new IOException("Failed to delete item"));
    }
  }

  private static class Holder {
    private static HistoryDao instance = new HistoryDao();
  }

  public static HistoryDao instance() {
    return Holder.instance;
  }

}
