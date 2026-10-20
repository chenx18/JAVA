package com.example.week4.common.exception;

public class DepartmentNotFoundException extends RuntimeException {

  public DepartmentNotFoundException(int id) {
    super("department not found by id: " + id);
  }

  public DepartmentNotFoundException(String name) {
    super("department not found by name: " + name);
  }
}