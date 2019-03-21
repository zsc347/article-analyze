package com.scaiz.analyze.parser;

import static org.junit.Assert.assertEquals;

import com.scaiz.analyze.TestUtils;
import org.junit.Test;

public class ParserTest {


  @Test
  public void testInvalidQuery() {
    TestUtils.assertParseException(() -> Parser.parse(null));
  }

  @Test
  public void testEmptyInput() {
    assertEquals("[]", Parser.parse("").toString());
    assertEquals("[]", Parser.parse("   ").toString());
  }

  @Test
  public void testOnlyPlainInput() {
    assertEquals("[query]", Parser.parse("query").toString());
  }

  @Test
  public void testDefaultOr() {
    assertEquals("[query1,query2]", Parser.parse("query1, query2").toString());
  }


  @Test
  public void testRedundant() {
    assertEquals("[(query)]", Parser.parse("[(query)]").toString());
  }

  @Test
  public void testEmptyCombine() {
    assertEquals("[(),(query)]", Parser.parse("[(), (query)]").toString());
  }
}