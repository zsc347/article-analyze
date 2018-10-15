package com.scaiz.analyze;

import com.scaiz.analyze.manager.DBManager;
import com.scaiz.analyze.parser.Parser;
import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.service.ArticleSearchService;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
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
import javax.management.DynamicMBean;

public class ArticleVerticle extends AbstractVerticle {

  private static int PORT = 8080;
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
    String query = context.request().params().get("query");
    List<Article> results;
    if (StringUtil.isNullOrEmpty(query)) {
      results = new LinkedList<>();
    } else {
      results = ArticleSearchService.instance()
          .search(Parser.parse(query));
    }
    res.put("query", context.request().params().get("query"));
    res.put("results", results);
    System.out.println(Json.encode(res));

    context.response().putHeader("content-type", "application/json");
    context.response().end(Json.encode(res));
  }

  public static void main(String[] args) throws Exception {
    DBManager.instance().load();
    vertx = Vertx.vertx();
    vertx.deployVerticle(new ArticleVerticle());
  }
}

