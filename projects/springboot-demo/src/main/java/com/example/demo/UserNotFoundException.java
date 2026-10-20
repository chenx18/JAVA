package com.example.demo;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(int id) {
    super("user not found: " + id);
  }
}