package com.example.week4.project.system.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DepartmentLog {

  private Integer id;

  private Integer departmentId;

  private String operation;

  private String content;

  private LocalDateTime createTime;
}
