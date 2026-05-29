package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public class RoleRepository {

  private final List<Role> roles = new ArrayList<>();

  public RoleRepository() {
    roles.add(new Role(1, "Admin"));
  }

  public List<Role> findAll() {
    return roles;
  }

  public Role findById(int id) {
    for(Role r : roles){
      if(r.getId() == id) {
        return r;
      }
    }
    return null;
  }

  public void save(Role r) {
    roles.add(r);
  }

  public boolean  existsById(int id) {
    return findById(id) != null;
  }

}