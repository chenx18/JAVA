package com.example.demo;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ApiResponse<String> handlerIllegalArgument(IllegalArgumentException e) {
    return ApiResponse.fail(400, e.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ApiResponse<String> handleUserNotFound(UserNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public ApiResponse<String> handleRoleNotFound(RoleNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<String> handlerOther(Exception e) {
    return ApiResponse.fail(500, "server error");
  }
}