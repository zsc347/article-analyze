package com.scaiz.analyze;

import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.pojo.History;
import com.scaiz.analyze.service.HistoryService;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ArticleVerticle extends AbstractVerticle {

  private static Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");

  private static int PORT = Integer.parseInt(
      System.getProperty("PORT", "8080"));

  @Override
  public void start() {
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route(HttpMethod.GET, "/api/search").blockingHandler(this::search);
    router.route(HttpMethod.GET, "/api/export").blockingHandler(this::download);

    router.route(HttpMethod.DELETE, "/api/history/:id")
        .blockingHandler(this::deleteHistory);
    router.route(HttpMethod.PUT, "/api/history")
        .blockingHandler(this::addHistory);
    router.route(HttpMethod.GET, "/api/history")
        .blockingHandler(this::fetchAllHistory);

    router.route("/*").handler(StaticHandler.create());
    router.route("/*").handler(ctx -> {
      if (!ctx.response().ended()) {
        ctx.reroute("/");
      }
    });
    httpServer.requestHandler(router::accept).listen(PORT);
  }

  private void fetchAllHistory(RoutingContext context) {
    List<History> all = HistoryService.instance().loadAll();
    context.response().putHeader("content-type", "application/json");
    log.debug("fetch history {}", all);
    context.response().end(Json.encode(all));
  }

  private void addHistory(RoutingContext context) {
    History history = Json.decodeValue(context.getBody(), History.class);
    history.setEpochTime(Instant.now().toEpochMilli());
    HistoryService.instance().save(history);
    log.debug("add history {}", history);
    context.response().end(Json.encode(history));
  }

  private void deleteHistory(RoutingContext context) {
    String idStr = context.pathParam("id");
    if (StringUtil.isNullOrEmpty(idStr)) {
      throw new IllegalStateException("param id not provided");
    }
    long id = Long.parseLong(idStr);
    log.debug("del history", id);
    boolean rs =  HistoryService.instance().remove(id);
    context.response().end(String.valueOf(rs));
  }


  private QueryResult query(MultiMap params) {
    QueryResult queryResult;
    String qStr = params.get("query");
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
    return queryResult;
  }


  private void download(RoutingContext context) {
    MultiMap params = context.request().params();
    String qStr = Optional.ofNullable(params.get("query")).orElse("null");
    log.info("export file name: {}", filterFilename(qStr));

    QueryResult queryResult = query(params);

    context.response().putHeader("Content-Type", "text/plain;charset=UTF-8");
    context.response().putHeader("Transfer-Encoding", "chunked");

    String encodeFilename;
    try {
      encodeFilename = URLEncoder.encode(filterFilename(qStr) + ".txt", StandardCharsets.UTF_8.toString());
    } catch (Exception e) {
      encodeFilename = "unknown.txt";
    }

    context.response().putHeader("Content-Disposition",
        "attachment;filename*=UTF-8''"+ encodeFilename);

    context.response().write("查询：" + params.get("query") + "\n");


    String filtered = params.get("filtered");
    Set<Integer> filterSet;
    if (!StringUtil.isNullOrEmpty(filtered)) {
      filterSet = Arrays.stream(filtered.split(","))
          .map(Integer::valueOf)
          .collect(Collectors.toSet());
    } else {
      filterSet = Collections.emptySet();
    }

    List<Article> cleanList = queryResult.getResults()
        .stream()
        .filter(a -> !filterSet.contains(a.getId()))
        .collect(Collectors.toList());
    context.response().write("共有 " + cleanList.size() + " 条记录\n");
    queryResult.getResults().forEach(result ->
        context.response().write(articleLine(result)));


    List<Article> removedList = queryResult.getResults()
        .stream()
        .filter(a -> filterSet.contains(a.getId()))
        .collect(Collectors.toList());
    if (removedList.size() > 0) {
      context.response().write("\n\n\n");
      context.response().write("过滤掉 " + removedList.size() + " 条记录\n");
      removedList.forEach(result -> context.response().write(articleLine(result)));
    }
    context.response().end();
  }


  private String articleLine(Article article) {
    return String.join(" ",
        String.valueOf(article.getId()),
        article.getTitle(),
        article.getAuthor(),
        article.getContent()) + "\n";
  }

  @SuppressWarnings("unchecked")
  private void search(RoutingContext context) {
    Map<String, Object> res = new HashMap<>();
    MultiMap params = context.request().params();
    QueryResult queryResult = query(params);
    res.put("query", Optional.of(params.get("query")).orElse(""));
    res.putAll(Json.decodeValue(Json.encode(queryResult), Map.class));
    context.response().putHeader("content-type", "application/json");
    context.response().end(Json.encode(res));
  }


  private static String filterFilename(String str) {
    return str == null ? "null" : FilePattern.matcher(str).replaceAll("");
  }


  public static void main(String[] args) {
    log.info("working folder: ", System.getProperty("user.dir"));
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ArticleVerticle());
  }
}

