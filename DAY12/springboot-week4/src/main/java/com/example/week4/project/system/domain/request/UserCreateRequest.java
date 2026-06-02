package com.example.week4.project.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCreateRequest {
  
  @Schema(description="用户名称")
  private String userName;

  private String nickName;

  private String email;

  private int status;
}
