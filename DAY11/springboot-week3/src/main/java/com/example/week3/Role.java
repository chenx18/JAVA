package com.example.week3;

public class Role {
  private int id;
  private String name;
  private String role;

  public Role(int id, String name, String role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }

  public Role() {
    
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  public void setName(String n) {
    name = n;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String r) {
    role = r;
  }

}
