package com.example.week3;

public class RoleNotFoundException extends RuntimeException {
  public RoleNotFoundException(int id){
    super("Role error id" + id);
  }
  
}
