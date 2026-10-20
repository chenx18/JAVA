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
import com.example.week4.project.system.domain.DepartmentLog;
import com.example.week4.project.system.domain.request.DepartmentCreateRequest;
import com.example.week4.project.system.domain.request.DepartmentQueryRequest;
import com.example.week4.project.system.domain.request.DepartmentUpdateRequest;
import com.example.week4.project.system.domain.vo.DepartmentDetailResponse;
import com.example.week4.project.system.domain.vo.DepartmentResponse;
import com.example.week4.project.system.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Tag(name = "Department", description = "Department management APIs")
@RestController
@RequestMapping("/departments")
public class DepartmentController {

  private final DepartmentService service;

  @Operation(summary = "Query departments by page")
  @GetMapping("/list")
  public ApiResponse<PageResponse<DepartmentResponse>> getDepartmentList(
      @Valid @ParameterObject DepartmentQueryRequest request) {
    return ApiResponse.success(service.getDepartmentPages(request));
  }

  @Operation(summary = "Query all departments")
  @GetMapping("/all")
  public ApiResponse<List<DepartmentResponse>> getDepartmentAll() {
    return ApiResponse.success(service.getAllDepartments());
  }

  @Operation(summary = "Query department by id")
  @GetMapping("/{id}")
  public ApiResponse<DepartmentResponse> getDepartmentById(
      @Parameter(description = "Department id") @PathVariable int id) {
    return ApiResponse.success(service.getDepartmentById(id));
  }

  @Operation(summary = "Query department by exact name")
  @GetMapping("/name/{name}")
  public ApiResponse<DepartmentResponse> getDepartmentByName(
      @Parameter(description = "Department name") @PathVariable String name) {
    return ApiResponse.success(service.getDepartmentByName(name));
  }

  @Operation(summary = "Search departments by name")
  @GetMapping("/search")
  public ApiResponse<List<DepartmentResponse>> searchDepartmentsByName(
      @Parameter(description = "Department name keyword") @RequestParam String name) {
    return ApiResponse.success(service.searchDepartmentsByName(name));
  }

  @Operation(summary = "Create department")
  @PostMapping("/add")
  public ApiResponse<DepartmentResponse> createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request) {
    return ApiResponse.success(service.createDepartment(request));
  }

  @Operation(summary = "Update department")
  @PutMapping("/update/{id}")
  public ApiResponse<DepartmentResponse> updateDepartment(
      @Parameter(description = "Department id") @PathVariable int id,
      @Valid @RequestBody DepartmentUpdateRequest request) {
    return ApiResponse.success(service.updateDepartment(id, request));
  }

  @Operation(summary = "Delete department")
  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteDepartment(
      @Parameter(description = "Department id") @PathVariable int id) {
    service.deleteDepartment(id);
    return ApiResponse.success(null);
  }

  @DeleteMapping("/batch")
  public ApiResponse<Void> batchDeleteDepartments(@RequestBody List<Integer> ids) {
    service.batchDeleteDepartments(ids);
    return ApiResponse.success(null);
  }

  @GetMapping("/{id}/logs")
  public ApiResponse<List<DepartmentLog>> getDepartmentLogs(
    @Parameter(description = "Department ID") @PathVariable int id) {
      return ApiResponse.success(service.getDepartmentLogs(id));
  }

  @GetMapping("/detail/{id}")
  public ApiResponse<DepartmentDetailResponse> getDepartmentDetail(
    @Parameter(description = "Department ID") @PathVariable int id
  ){
    return ApiResponse.success(service.getDepartmentDetails(id));
  }
  
  
}
