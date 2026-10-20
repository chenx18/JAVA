package com.example.week4.project.system.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "username is blank")
  private String userName;

  @NotBlank(message = "password is blank")
  private String password;
}