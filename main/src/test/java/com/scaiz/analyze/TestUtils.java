package com.scaiz.analyze;

import com.scaiz.analyze.parser.ParseError;

public class TestUtils {

  public static void assertNullPointerException(Runnable runnable) {
    try {
      runnable.run();
      fail("Should throw NullPointerException");
    } catch (NullPointerException e) {
      // OK
    }
  }

  public static void assertParseException(Runnable runnable) {
    try {
      runnable.run();
      fail("Should throw NullPointerException");
    } catch (ParseError e) {
      // OK
    }
  }


  static public void fail(String message) {
    if (message == null) {
      throw new AssertionError();
    }
    throw new AssertionError(message);
  }
}
