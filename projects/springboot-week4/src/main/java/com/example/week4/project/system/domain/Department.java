package com.example.week4.project.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

  @Schema(description = "部门ID")
  @Min(value = 1, message = "部门ID必须大于0")
  private int id;

  @Schema(description = "部门名称")
  @NotBlank(message = "部门名称不能为空")
  private String name;

  @Schema(description = "部门描述")
  private String description;

  @Schema(description = "部门状态")
  private Integer status;

}
