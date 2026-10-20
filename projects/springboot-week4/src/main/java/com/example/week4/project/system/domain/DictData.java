package com.example.week4.project.system.domain;

import lombok.Data;

@Data
public class DictData {
  private Integer id;
  private String dictType;
  private String dictLabel;
  private String dictValue;
  private Integer status;
  private String remark;

  private Integer pageNum = 1;
  private Integer pageSize = 10;
}
