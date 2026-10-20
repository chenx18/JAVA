package com.example.week3;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("department")
public class DepartmentController {
  private final DepartmentService service;
  
  public DepartmentController(DepartmentService s) {
    this.service = s;
  }

  @GetMapping("list")
  public ApiResponse<List<Department>> getDepartmentList() {
    return ApiResponse.success(service.getAllDepartments());
  }

  @GetMapping("/{id}")
  public ApiResponse<Department> getDepartment(@PathVariable int id) {
    return ApiResponse.success(service.getDepartmentById(id));
  }
  
  @PostMapping("/add")
  public ApiResponse<Department> createDepartment(@RequestBody Department d) {
    return ApiResponse.success(service.createDepartment(d));
  }

  @PutMapping("update/{id}")
  public ApiResponse<Department> updateDepartment(@PathVariable int id, @RequestBody Department d) {
    return ApiResponse.success(service.updateDepartment(id, d));
  }

  @DeleteMapping("delete/{id}")
  public ApiResponse<Void> deleteDepartment(@PathVariable int id) {
    service.deleteDepartment(id);
    return ApiResponse.success(null);
  }
  

}
