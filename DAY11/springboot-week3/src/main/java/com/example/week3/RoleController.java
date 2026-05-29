package com.example.week3;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/roles")
public class RoleController {
  private final RoleService service;

  public RoleController(RoleService s) {
    this.service = s;
  }

  @GetMapping("/list")
  public ApiResponse<List<Role>> getRoleList(){
    return ApiResponse.success(service.getAllRoles());
  }

  @GetMapping("/{id}")
  public ApiResponse<Role> getRoleById(@PathVariable int id) {
    return ApiResponse.success(service.getRoleById(id));
  }
  
  
  @PostMapping("/add")
  public ApiResponse<Role> createRole(@RequestBody Role r) {
    return ApiResponse.success(service.createRole(r));
  }

  @PutMapping("update/{id}")
  public ApiResponse<Role> updateRole(@PathVariable int id, @RequestBody Role r) {
    return ApiResponse.success(service.updateRole(id, r));
  }

  @DeleteMapping("delete/{id}")
  public ApiResponse<Void> deleteRole(@PathVariable int id) {
    service.deleteRole(id);
    return ApiResponse.success(null);
  }
  
}
