package com.example.demo;

public class RoleNotFoundException extends RuntimeException {
  public RoleNotFoundException(int id) {
    super("Role id: " + id + "error");
  }
}