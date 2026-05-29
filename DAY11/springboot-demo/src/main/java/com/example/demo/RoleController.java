package com.example.demo;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService service) {
    this.roleService = service;
  }

  @GetMapping
  public ApiResponse<List<Role>> getAllRoles() {
    return ApiResponse.success(roleService.getAllRole());
  }

  @GetMapping("/{id}")
  public ApiResponse<Role> getRoleById(@PathVariable int id) {
    return ApiResponse.success(roleService.getRoleById(id));
  }

  @PostMapping
  public ApiResponse<Role> addRole(@RequestBody Role role) {
    return ApiResponse.success(roleService.addRole(role));
  }
}