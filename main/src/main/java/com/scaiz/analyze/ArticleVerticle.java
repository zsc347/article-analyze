package com.scaiz.analyze;

import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.service.SearchService;
import com.scaiz.analyze.spec.Query;
import com.scaiz.analyze.spec.Query.QueryBuilder;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArticleVerticle extends AbstractVerticle {

  private static int PORT = Integer.parseInt(
      System.getProperty("PORT", "8080"));
  private static Vertx vertx;

  @Override
  public void start() {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route("/api/search").blockingHandler(this::search);
    httpServer.requestHandler(router::accept).listen(PORT);
  }


  private void search(RoutingContext context) {
    Map<String, Object> res = new HashMap<>();

    MultiMap params = context.request().params();
    String qStr = params.get("query");

    List<Article> results;
    if (StringUtil.isNullOrEmpty(qStr)) {
      results = new LinkedList<>();
    } else {
      QueryBuilder builder = Query.builder();
      builder.corpus(Optional.ofNullable(params.get("corpus"))
          .orElse("tangshi"));
      if (!StringUtil.isNullOrEmpty(params.get("from"))) {
        builder.from(Integer.parseInt(params.get("from")));
      } else {
        builder.from(0);
      }
      if (!StringUtil.isNullOrEmpty(params.get("size"))) {
        builder.size(Integer.parseInt(params.get("size")));
      } else {
        builder.size(10);
      }
      builder.query(qStr);
      Query query = builder.build();
      results = SearchService.instance().search(query);
    }
    res.put("query", Optional.of(params.get("query")).orElse(""));
    res.put("results", results);
    context.response().putHeader("content-type", "application/json");
    context.response().end(Json.encode(res));
  }

  public static void main(String[] args) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(new ArticleVerticle());
  }
}

