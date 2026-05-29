package com.example.week4.project.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Department create request")
public class DepartmentCreateRequest {

  @Schema(description = "Department name", example = "Research")
  @NotBlank(message = "department name cannot be blank")
  private String name;

  @Schema(description = "Department description", example = "Responsible for product research")
  private String description;

  @Schema(description = "Department status", example = "1")
  private Integer status;
}