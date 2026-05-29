package com.example.week3;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(int id) {
    super("user id " + id + "error");
  }
  
}