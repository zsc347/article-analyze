package com.scaiz.analyze.spec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class PlainCondition extends Condition {

  private String word;

  @Override
  public String toString() {
    return word;
  }
}
