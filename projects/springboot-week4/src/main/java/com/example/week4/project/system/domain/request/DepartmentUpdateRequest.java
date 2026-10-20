package com.example.week4.project.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Department update request")
public class DepartmentUpdateRequest {

  @Schema(description = "Department name", example = "Research Center")
  private String name;

  @Schema(description = "Department description", example = "Responsible for product research")
  private String description;

  @Schema(description = "Department status", example = "1")
  private Integer status;
}