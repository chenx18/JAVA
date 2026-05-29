package com.example.week3;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ExceptionHandler() 处理指定的异常
// @RestControllerAdvice 表示这是一个全局异常处理类
// 只也要controller service 抛出异常 spring 就会来这里找对应方法

// @RestControllerAdvice 表示这是一个全局异常处理类
// 只要 Controller / service 抛出异常，Spring 就会来这里找对应的处理方法
// 它的返回值也会自动转成json
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ExceptionHandler(IllegalArgumentException.class)
  // 表示：专门处理 IllegalArgumentException 这种异常
  // IllegalArgumentException 一般表示 参数不合法
  // 列如 id<=0、name 为空、重复新增等

  @ExceptionHandler(IllegalArgumentException.class)
  public ApiResponse<String> handleIllegakArgument(IllegalArgumentException e) {
    return ApiResponse.fail(400, e.getMessage());
  }
  
  // ExceptionHandler(UserNotFoundException.class)
  // 表示 专门处理 UserNotFoundException 这种业务
  // UserNotFoundException 是我们定义的业务异常

  @ExceptionHandler(UserNotFoundException.class)
  public ApiResponse<String> handleUserNotFound(UserNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }


  // ExceptionHandler(RoleNotFoundException.class)
  // 表示专门处理 RoleNotFoundException 业务的异常

  @ExceptionHandler(RoleNotFoundException.class)
  public ApiResponse<String> handleRoleNotFound(RoleNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }

  // ExceptionHandler 
  @ExceptionHandler(DepartmentNotFoundException.class)
  public ApiResponse<String> handelDepartmentNotFound(DepartmentNotFoundException e) {
    return ApiResponse.fail(404, e.getMessage());
  }

  // @ExceptionHandler(Exception.class)
  // 表示：处理所有没有被上面方法处理掉的异常
  // Exception 是大兜底，防止系统直接把错误堆栈暴露给前端
  // 通常返回 500，表示服务器内部错误
  @ExceptionHandler(Exception.class)
  public ApiResponse<String> handleOther(Exception e) {
    return ApiResponse.fail(500, "service error");
  }

}

