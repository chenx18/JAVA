package com.example.week3;

public class DepartmentNotFoundException extends RuntimeException {

  public DepartmentNotFoundException(int id) {
    super("Department id " + id + " error");
  }
  
}
