package com.scaiz.analyze.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.scaiz.analyze.manager.DBManager;
import com.scaiz.analyze.pojo.Article;
import com.scaiz.analyze.spec.CombineCondition;
import com.scaiz.analyze.spec.Condition;
import com.scaiz.analyze.spec.Operator;
import com.scaiz.analyze.spec.PlainCondition;
import com.scaiz.analyze.spec.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class SearchServiceTest {

  private DBManager manager;
  private SearchService searchService;

  @Before
  public void setUp() {
    searchService = SearchService.instance();
    manager = DBManager.loadCorpus("test");
  }

  @Test
  public void testPlain() {
    Condition con = new PlainCondition("");
    Set<Integer> rs = searchService.search(manager, con);
    assertNotNull(rs);
    assertEquals(3, rs.size());

    con = new PlainCondition("bb");
    rs = searchService.search(manager, con);
    assertEquals(2, rs.size());
    assertTrue(rs.containsAll(Arrays.asList(1, 2)));
  }

  @Test
  public void testCombine() {
    Condition or = new CombineCondition(Operator.OR, Arrays.asList(
        new PlainCondition("aa"),
        new PlainCondition("ee")
    ));
    Set<Integer> rs = searchService.search(manager, or);
    assertEquals(2, rs.size());
    assertTrue(rs.containsAll(Arrays.asList(1, 3)));

    Condition and = new CombineCondition(Operator.AND, Arrays.asList(
        new PlainCondition("aa"),
        new PlainCondition("bb"),
        new PlainCondition("cc")
    ));
    rs = searchService.search(manager, and);
    assertEquals(1, rs.size());
    assertTrue(rs.containsAll(Arrays.asList(1)));
  }

  @Test
  public void testQuery() {
    List<Article> rs = searchService.search(Query.builder()
        .corpus("test")
        .query("[aa, cc]")
        .from(0)
        .size(100)
        .build()).getResults();
    assertEquals(3, rs.size());
  }
}
