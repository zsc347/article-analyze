package com.scaiz.analyze.manager;

import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.pojo.Article.ArticleBuilder;
import io.vertx.core.json.Json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBManager {

  private static ConcurrentMap<String, DBManager> cache = new ConcurrentHashMap<>();

  private List<Article> articles = new LinkedList<>();

  private void load(String corpus) throws IOException {
    InputStream inputStream = DBManager.class.getClassLoader()
        .getResourceAsStream(corpus + ".txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    String str;

    while ((str = br.readLine()) != null) {
      String[] parts = str.split(";");
      ArticleBuilder builder = Article.builder()
          .author("")
          .title("")
          .content("");
      if (parts.length > 0) {
        builder.id(Integer.valueOf(parts[0]));
      }
      if (parts.length > 1) {
        builder.title(parts[1]);
      }
      if (parts.length > 2) {
        builder.author(parts[2]);
      }
      if (parts.length > 3) {
        builder.content(parts[3]);
      }
      articles.add(builder.build());
    }
  }


  public static DBManager loadCorpus(String corpus) {
    try {
      DBManager manager = cache.get(corpus);
      if (manager == null) {
        manager = new DBManager();
        manager.load(corpus);
        cache.putIfAbsent(corpus, manager);
      }
      return manager;
    } catch (IOException e) {
      log.error("load corpus {} error", corpus, e);
      return new DBManager();
    }
  }

  public List<Article> getArticles() {
    return articles;
  }
}
