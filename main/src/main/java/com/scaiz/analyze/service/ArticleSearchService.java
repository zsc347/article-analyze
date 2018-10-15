package com.scaiz.analyze.service;

import com.scaiz.analyze.manager.DBManager;
import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.spec.Query;
import java.util.List;

public class ArticleSearchService {

  public List<Article> search(Query query) {
    return DBManager.instance().getArticles().subList(0,10);
  }


  private static class ArticleSearchServiceHolder {
    private static ArticleSearchService instance = new ArticleSearchService();
  }

  public static ArticleSearchService instance() {
    return ArticleSearchService.ArticleSearchServiceHolder.instance;
  }

}
