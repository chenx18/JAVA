package com.example.week4.project.system.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.week4.common.response.ApiResponse;
import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.request.DepartmentCreateRequest;
import com.example.week4.project.system.domain.request.DepartmentQueryRequest;
import com.example.week4.project.system.domain.request.DepartmentUpdateRequest;
import com.example.week4.project.system.domain.vo.DepartmentResponse;
import com.example.week4.project.system.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Tag(name = "閮ㄩ棬绠＄悊", description = "閮ㄩ棬澧炲垹鏀规煡鎺ュ彛")
@RestController
@RequestMapping("/departments")
public class DepartmentController {

  private final DepartmentService service;
  
  // 浣跨敤浜?Lombok 鐨?RequiredArgsConstructor鐪佹帀涓嬮潰浠ｇ爜
  // @RequiredArgsConstructor 浼氳嚜鍔ㄧ敓鎴愶細
  // public DepartmentController(DepartmentService service) {
  //   this.service = service;
  // }

  @Operation(summary = "鍒嗛〉鏌ヨ閮ㄩ棬鍒楄〃")
  @GetMapping("/list")
  public ApiResponse<PageResponse<DepartmentResponse>> getDepartmentList(
    @Valid @ParameterObject DepartmentQueryRequest request
  ) {
    return ApiResponse.success(service.getDepartmentPages(request));
  }

  @Operation(summary = "鏌ヨ閮ㄩ棬鍒楄〃")
  @GetMapping("/all")
  public ApiResponse<List<DepartmentResponse>> getDepartmentAll() {
    return ApiResponse.success(service.getAllDepartments());
  }

  @Operation(summary = "鏍规嵁 ID 鏌ヨ閮ㄩ棬")
  @GetMapping("/{id}")
  public ApiResponse<DepartmentResponse> getDepartmentById(
    @Parameter(description = "閮ㄩ棬 ID") @PathVariable int id) {
    return ApiResponse.success(service.getDepartmentById(id));
  }

  // 濡傛灉鐩存帴/{name} 浼氬拰 涓婇潰鐨?/{id} 鍐茬獊
  @Operation(summary = "鏍规嵁 name 鏌ヨ閮ㄩ棬")
  @GetMapping("/name/{name}")
  public ApiResponse<DepartmentResponse> getDepartmentByName(
    @Parameter(description = "閮ㄩ棬鍚嶇О") @PathVariable String name) {
    return ApiResponse.success(service.getDepartmentByName(name));
  }

  @Operation(summary = "鏍规嵁 name 妯＄硦鏌ヨ")
  @GetMapping("/search")
  public ApiResponse<List<DepartmentResponse>> searchDepartmentsByName(
    @Parameter(description = "閮ㄩ棬鍚嶇О") @RequestParam String name
  ){
    return ApiResponse.success(service.searchDepartmentsByName(name));
  }
  
  @Operation(summary = "鏂板閮ㄩ棬")
  @PostMapping("/add")
  public ApiResponse<DepartmentResponse> createDepartment(
    @Valid @RequestBody DepartmentCreateRequest request) {
    return ApiResponse.success(service.createDepartment(request));
  }

  @Operation(summary = "淇敼閮ㄩ棬")
  @PutMapping("/update/{id}")
  public ApiResponse<DepartmentResponse> updateDepartment(
    @Parameter(description = "閮ㄩ棬 ID") @PathVariable int id,
    @Valid @RequestBody DepartmentUpdateRequest request) {
    return ApiResponse.success(service.updateDepartment(id, request));
  }

  @Operation(summary = "鍒犻櫎閮ㄩ棬")
  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteDepartment(
    @Parameter(description = "閮ㄩ棬 ID") @PathVariable int id) {
    service.deleteDepartment(id);
    return ApiResponse.success(null);
  }
  
}