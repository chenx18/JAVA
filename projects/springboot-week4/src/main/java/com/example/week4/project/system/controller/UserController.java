package com.example.week4.project.system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.week4.common.response.ApiResponse;
import com.example.week4.common.response.PageResponse;
import com.example.week4.framework.security.LoginUser;
import com.example.week4.framework.security.LoginUserContext;
import com.example.week4.project.system.domain.User;
import com.example.week4.project.system.domain.request.LoginRequest;
import com.example.week4.project.system.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;




@Tag(name="用户管理", description="用户增删改查")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService service;

  @Operation(summary="用户列表分页查询")
  @GetMapping("/list")
  public ApiResponse<PageResponse<User>> getUserlist(
    @ParameterObject User request) {
    return ApiResponse.success(service.getUsersList(request));
  }

  @Operation(summary="用户新增")
  @PostMapping("/create")
  public ApiResponse<Void> createUser(
    @Valid @RequestBody User request
  ){
    service.createUser(request);
    return ApiResponse.success(null);
  }

  @Operation(summary="用户更新")
  @PutMapping("/update/{id}")
  public ApiResponse<Void> updateUser(
    @PathVariable int id, 
    @RequestBody User request) {
      service.updateUser(id, request);
      return ApiResponse.success(null);
  }


  @Operation(summary="用户删除")
  @DeleteMapping("/delete/{ids}")
  public ApiResponse<Void> deleteUser(
    @PathVariable List<Integer> ids
  ){
    service.deleteByIds(ids);
    return ApiResponse.success(null);
  }
  

  @PostMapping("/login")
  public ApiResponse<String> login(@RequestBody LoginRequest request) {
    return ApiResponse.success(service.login(request));
  }
  
  @GetMapping("/current")
  public ApiResponse<LoginUser> currentUser() {
    return ApiResponse.success(LoginUserContext.get());
  }

  @GetMapping("/info")
  public ApiResponse<LoginUser> getUserInfo() {
    return ApiResponse.success(LoginUserContext.get());
  }
  
  
  
}