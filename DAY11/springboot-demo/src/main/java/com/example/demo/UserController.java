package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService service;

  public UserController(UserService userService) {
    this.service = userService;
  }

  @GetMapping("/{id}")
  public ApiResponse<User> getUserById(@PathVariable int id) {
    return ApiResponse.success(service.getUserById(id));
  }

  @PostMapping
  public ApiResponse<User> addUser(@RequestBody User user) {
    return ApiResponse.success(service.addUser(user));
  }
}