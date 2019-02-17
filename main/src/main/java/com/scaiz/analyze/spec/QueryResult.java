package com.scaiz.analyze.spec;

import com.scaiz.analyze.pojo.Article;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryResult {
  List<String> keys;
  List<Article> results;
  Long total;
}
