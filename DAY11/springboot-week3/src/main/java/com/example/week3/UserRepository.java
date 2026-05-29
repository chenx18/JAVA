package com.example.week3;

import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final List<User> list = new ArrayList<>();

  public UserRepository() {
    list.add(new User(1, "Admin"));
  }

  public List<User> findAll() {
    return list;
  }

  public User findById(int id) {
    for (User u : list) {
      if(u.getId() == id) {
        return u;
      }
    }
    return null;
  }

  public void save(User u) {
    list.add(u);
  }

  public User updateById(int id, User u) {
    User user = findById(id);
    if(user == null) {
      return null;
    }
    user.setName(u.getName());
    return user;
  }

  public boolean deleteById(int id) {
    User user = findById(id);
    if(user == null) {
      return false;
    }
    list.remove(user);
    return true;
  }

  public boolean existsById(int id) {
    return !(findById(id) == null);
  }

}