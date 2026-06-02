package com.example.week4.project.system.domain.vo;

import java.util.List;

import com.example.week4.project.system.domain.DepartmentLog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DepartmentDetailResponse {

  @Schema(description="Department id")
  private Integer id;

  @Schema(description="Department name")
  private String name;

  @Schema(description="描述")
  private String description;

  @Schema(description="状态")
  private Integer status;

  @Schema(description="操作日志")
  private List<DepartmentLog> logs;
}
