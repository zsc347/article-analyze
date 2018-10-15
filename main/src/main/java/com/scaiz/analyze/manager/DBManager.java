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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBManager {

  private List<Article> articles = new LinkedList<Article>();


  public void load() throws IOException {
    InputStream inputStream = DBManager.class.getClassLoader()
        .getResourceAsStream("tangshi.txt");
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

    System.out.println(Json.encode(articles));
  }

  public List<Article> getArticles() {
    return articles;
  }

  private static class DBManagerHolder {
    private static DBManager instance = new DBManager();
  }

  public static DBManager instance() {
    return DBManagerHolder.instance;
  }

  public static void main(String[] args) throws Exception {
    DBManager.instance().load();
  }
}
