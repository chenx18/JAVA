package com.example.week4.project.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Department response object")
public class DepartmentResponse {

  @Schema(description = "Department ID", example = "1")
  private int id;

  @Schema(description = "Department name", example = "Research")
  private String name;

  @Schema(description = "Department description", example = "Responsible for product research")
  private String description;

  @Schema(description = "Department status", example = "1")
  private Integer status;
}