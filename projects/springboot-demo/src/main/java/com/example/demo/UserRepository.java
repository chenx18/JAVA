package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final List<User> users = new ArrayList<>();

  public UserRepository() {
    users.add(new User(1, "CocO", "741852@163.com"));
    users.add(new User(2, "LILI", "741852@123.com"));
  }

  public User findById(int id) {
    for (User u : users) {
      if (u.getId() == id) {
        return u;
      }
    }
    return null;
  }

  public void save(User u) {
    users.add(u);
  }

  public boolean existsById(int id) {
    return findById(id) != null;
  }
}