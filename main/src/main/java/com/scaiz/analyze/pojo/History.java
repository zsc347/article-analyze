package com.scaiz.analyze.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;


@Data
@JsonInclude(Include.NON_NULL)
public class History {
  @JsonProperty("id")
  long epochTime;
  String title;
  String query;
  List<Integer> filtered;
}
