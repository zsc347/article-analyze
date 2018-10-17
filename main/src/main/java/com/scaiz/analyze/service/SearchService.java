package com.scaiz.analyze.service;

import com.scaiz.analyze.manager.DBManager;
import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.spec.Query;
import java.util.List;

public class SearchService {

  public List<Article> search(Query query) {
    return DBManager.instance().getArticles().subList(0,10000);
  }

  private static class ArticleSearchServiceHolder {
    private static SearchService instance = new SearchService();
  }

  public static SearchService instance() {
    return SearchService.ArticleSearchServiceHolder.instance;
  }
}
