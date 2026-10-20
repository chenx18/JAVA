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
public class User {

  @Schema(description="用户ID")
  @Min(value=1, message="用户ID必须大于0")
  private int id;

  @Schema(description = "密码")
  private String password;

  @Schema(description="用户名称")
  private String userName;

  @Schema(description="用户昵称")
  private String nickName;

  @Schema(description="Email")
  private String email;

  @Schema(description="用户状态")
  private Integer status;

  @Schema(description="页码")
  private Integer pageNum = 1;

  @Schema(description="每页数量")
  private Integer pageSize = 10;
}
