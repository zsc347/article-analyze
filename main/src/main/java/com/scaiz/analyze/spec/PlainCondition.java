package com.scaiz.analyze.spec;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlainCondition extends Condition {

  private String word;

  @Override
  public String toString() {
    return word;
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
    return toString().hashCode();
  }

  @Override
  public List<String> keys() {
    return Collections.singletonList(word);
  }
}
