package com.example.week4.project.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Department query request")
public class DepartmentQueryRequest {

  @Schema(description = "Page number", example = "1")
  @Min(value = 1, message = "pageNum must > 0")
  private int pageNum = 1;

  @Schema(description = "Page size", example = "10")
  @Min(value = 1, message = "pageSize must > 0")
  private int pageSize = 10;

  @Schema(description = "Department name", example = "Research")
  private String name;

  @Schema(description = "Department description")
  private String description;

  @Schema(description = "Department status", example = "1")
  private Integer status;
}