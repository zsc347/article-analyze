package com.scaiz.analyze.parser;

import com.scaiz.analyze.spec.CombineCondition;
import com.scaiz.analyze.spec.Condition;
import com.scaiz.analyze.spec.Operator;
import com.scaiz.analyze.spec.PlainCondition;
import io.netty.util.internal.StringUtil;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

public class Parser {


  /**
   * parse string like [(con1, con2), (con3, con4)]
   * <p/>
   * (con1, con2) means con1 and con2
   * <p/>
   * [con1,con2] means con1 or con2
   */
  public static Condition parse(String queryString) {
    Objects.requireNonNull(queryString);
    if (!queryString.startsWith("[") && !queryString.startsWith("(")) {
      queryString = "[" + queryString + "]";
    }

    Stack<Condition> stack = new Stack<>();
    StringBuilder sb = new StringBuilder();
    int i = 0;

    Condition cur = null;
    while (i < queryString.length()) {
      char c = queryString.charAt(i);
      if (c == '[') {
        cur = new CombineCondition(Operator.OR, new LinkedList<>());
        stack.push(cur);
      } else if (c == '(') {
        cur = new CombineCondition(Operator.AND, new LinkedList<>());
        stack.push(cur);
      } else if (c == ')' || c == ']') {
        if (cur instanceof CombineCondition &&
            ((CombineCondition) cur).getConditions().isEmpty()) {
          i++;
          continue;
        }
        stack.pop();
        Condition parent = stack.peek();
        if (!(parent instanceof CombineCondition)) {
          throw new ParseError("parse error at " + i);
        }
        if (cur instanceof PlainCondition) {
          ((PlainCondition) cur).setWord(sb.toString());
          sb.delete(0, sb.length());
        }
        ((CombineCondition) parent).getConditions().add(cur);
        cur = parent;
      } else if (c == ',' || c == '，') {
        stack.pop();
        Condition parent = stack.peek();
        if (!(parent instanceof CombineCondition)) {
          throw new ParseError("parse error at " + i);
        }
        if (cur instanceof PlainCondition) {
          ((PlainCondition) cur).setWord(sb.toString());
          sb.delete(0, sb.length());
        }
        ((CombineCondition) parent).getConditions().add(cur);
      } else if (c != ' ') {
        if (sb.length() == 0) {
          cur = new PlainCondition();
          stack.push(cur);
        }
        sb.append(c);
      }
      i++;
    }
    if (!(stack.size() == 1)) {
      throw new ParseError("partial request: " + cur.toString());
    }
    return Optional.ofNullable(cur).orElse(new PlainCondition(""));
  }
}
