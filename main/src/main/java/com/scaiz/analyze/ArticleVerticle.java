package com.scaiz.analyze;

import com.scaiz.analyze.service.SearchService;
import com.scaiz.analyze.spec.Query;
import com.scaiz.analyze.spec.Query.QueryBuilder;
import com.scaiz.analyze.spec.QueryResult;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class ArticleVerticle extends AbstractVerticle {

  private static int PORT = Integer.parseInt(
      System.getProperty("PORT", "8080"));

  @Override
  public void start() {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route(HttpMethod.GET,"/api/search").blockingHandler(this::search);

    router.route("/*").handler(StaticHandler.create());
    router.route("/*").handler(ctx -> {
      if(!ctx.response().ended()) {
        ctx.reroute("/");
      }
    });
    httpServer.requestHandler(router::accept).listen(PORT);
  }


  @SuppressWarnings("unchecked")
  private void search(RoutingContext context) {
    Map<String, Object> res = new HashMap<>();

    MultiMap params = context.request().params();
    String qStr = params.get("query");

    QueryResult queryResult;
    if (StringUtil.isNullOrEmpty(qStr)) {
      queryResult = new QueryResult(Collections.emptyList(), Collections.emptyList(), 0L);
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
      queryResult = SearchService.instance().search(query);
    }
    res.put("query", Optional.of(params.get("query")).orElse(""));
    res.putAll(Json.decodeValue(Json.encode(queryResult), Map.class));
    context.response().putHeader("content-type", "application/json");
    context.response().end(Json.encode(res));
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ArticleVerticle());
  }
}

