package com.scaiz.analyze.spec;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

@Value
public class CombineCondition extends Condition {

  private Operator op;
  private List<Condition> conditions;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (Operator.AND.equals(op)) {
      sb.append("(");
    } else {
      sb.append("[");
    }
    sb.append(String.join(",", conditions.stream().map(Object::toString)
        .collect(Collectors.toList())));
    if (Operator.AND.equals(op)) {
      sb.append(")");
    } else {
      sb.append("]");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof PlainCondition)) {
      return false;
    }
    return this.toString().equals(that.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}
