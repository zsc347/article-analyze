package com.scaiz.analyze.service;

import com.scaiz.analyze.dao.HistoryDao;
import com.scaiz.analyze.pojo.History;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryService {


  public void save(History history) {
    HistoryDao.instance().save(history);
  }

  public boolean remove(long id) {
    return HistoryDao.instance().remove(id);
  }

  public List<History> loadAll() {
    return HistoryDao.instance().loadAll();
  }

  private static class HistoryServiceHolder {
    private static HistoryService instance = new HistoryService();
  }

  public static HistoryService instance() {
    return HistoryService.HistoryServiceHolder.instance;
  }
}
