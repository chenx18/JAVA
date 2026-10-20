package com.example.week3;

public class Department {

  private int id;
  private String name;

  public Department(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public Department() {}

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    this.name = n;
  }
  
}
