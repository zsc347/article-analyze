package com.scaiz.analyze.spec;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Query {
  private String corpus;
  private String query;
  private Integer from;
  private Integer size;
}
