package com.example.week3;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




// RestController
// 作用：声明这是接口类，返回值自动变json。
@RestController

// RequestMapping("/user") 
// 作用：给整个 controller 加统一路径前缀
@RequestMapping("user")
public class UserController {

  private final UserService service;

  public UserController(UserService s){
    this.service = s;
  }

  // GetMapping 表示：处理 GET 请求
  @GetMapping("/list")
  public ApiResponse<List<User>> getAllUser() {
    return ApiResponse.success(service.getAllUsers());
  }

  // PathVariable 表示：从路径中取出 id
  @GetMapping("/{id}")
  public ApiResponse<User> getUser(@PathVariable int id) {
    return ApiResponse.success(service.getUserById(id));
  }
  
  // RequestBody 表示：从请求体 JSON 中接收 User 对象
  @PostMapping("/add")
  public ApiResponse<User> createUser(@RequestBody User u) {
    return ApiResponse.success((service.createUser(u)));
  }

  @PutMapping("/{id}")
  public ApiResponse<User> updateUser(@PathVariable int id, User u) {
    return ApiResponse.success(service.updateUser(id, u));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteUser(@PathVariable int id) {
    service.deleteUser(id);
    return ApiResponse.success(null);
  }

}

// @RestController 声明接口类
// @RequestMapping 统一路径前缀
// @GetMapping 查询接口
// @PostMapping 新增接口
// @PutMapping 修改接口
// @DeleteMapping 删除接口
// @PathVariable 接路径参数
// @RequestBody 接 json请求体
// @RestControllerAdvice 全局异常处理
// @ExceptionHandler 指定异常处理方法