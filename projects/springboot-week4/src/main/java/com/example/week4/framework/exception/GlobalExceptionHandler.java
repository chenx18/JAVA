package com.example.week4.framework.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.week4.common.exception.DepartmentNotFoundException;
import com.example.week4.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ApiResponse<String> handleIllegalArgument(IllegalArgumentException e) {
    return ApiResponse.fail(400, e.getMessage());
  }

  @ExceptionHandler(DepartmentNotFoundException.class)
  public ApiResponse<String> handleDepartmentException(DepartmentNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ApiResponse<Void> handleDuplicateKey(DuplicateKeyException e) {
    return ApiResponse.fail(400, "data already exists");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
    String message = "parameter validation failed";

    if (e.getBindingResult().getFieldError() != null) {
      message = e.getBindingResult().getFieldError().getDefaultMessage();
    }

    return ApiResponse.fail(400, message);
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<String> handleOtherException(Exception e) {
    e.printStackTrace();
    return ApiResponse.fail(500, e.getMessage());
  }
}