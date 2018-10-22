package com.scaiz.analyze.service;

import com.scaiz.analyze.manager.DBManager;
import com.scaiz.analyze.parser.Parser;
import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.spec.CombineCondition;
import com.scaiz.analyze.spec.Condition;
import com.scaiz.analyze.spec.PlainCondition;
import com.scaiz.analyze.spec.Query;
import com.scaiz.analyze.spec.QueryResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchService {

  public QueryResult search(Query query) {
    DBManager manager = DBManager.loadCorpus(query.getCorpus());
    List<Integer> idList = new ArrayList<>(this.search(manager,
        Parser.parse(query.getQuery())));
    Collections.sort(idList);
    List<Article> all = idList.stream()
        .map(id -> manager.getArticles().get(id - 1))
        .collect(Collectors.toList());
    return new QueryResult(all.subList(query.getFrom(),
        Math.min(query.getFrom() + query.getSize(), all.size())), (long) all.size());
  }

  Set<Integer> search(DBManager manager, Condition condition) {
    if (condition instanceof PlainCondition) {
      return searchPlain(manager, (PlainCondition) condition);
    } else if (condition instanceof CombineCondition) {
      return searchCombine(manager, (CombineCondition) condition);
    } else {
      throw new UnsupportedOperationException();
    }
  }


  private Set<Integer> searchPlain(DBManager manager, PlainCondition plain) {
    String word = plain.getWord();
    return manager.getArticles().parallelStream()
        .filter(article -> article.getContent().contains(word))
        .map(Article::getId)
        .collect(Collectors.toSet());
  }

  private Set<Integer> searchCombine(DBManager manager, CombineCondition condition) {
    if (condition.getConditions().isEmpty()) {
      return Collections.emptySet();
    }
    switch (condition.getOp()) {
      case OR:
        return condition.getConditions()
            .stream()
            .map(con -> this.search(manager, con))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
      case AND:
        List<Condition> cons = condition.getConditions();
        Set<Integer> result = this.search(manager, cons.get(0));
        for (int i = 1; i < cons.size(); i++) {
          result.retainAll(this.search(manager, cons.get(i)));
        }
        return result;
    }
    return Collections.emptySet();
  }

  private static class ArticleSearchServiceHolder {

    private static SearchService instance = new SearchService();
  }

  public static SearchService instance() {
    return SearchService.ArticleSearchServiceHolder.instance;
  }
}
