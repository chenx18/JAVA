package com.example.week3;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {
  private final List<Role> list = new ArrayList<>();

  public RoleRepository() {
    list.add(new Role(1, "超级管理员", "Admin"));
  }

  public List<Role> findAll() {
    return list;
  }
  
  public Role findById(int id) {
    for(Role r : list) {
      if(r.getId() == id) {
        return r;
      }
    }
    return null;
  }

  public Role saveRole(Role r) {
    list.add(r);
    return r;
  }

  public Role updateRoleById(int id, Role r) {
    Role role = findById(id);
    if(role == null) {
      return null;
    }
    role.setName(r.getName());
    role.setRole(r.getRole());
    return role;
  }

  public boolean  deleteRoleById(int id) {
    Role role = findById(id);
    if(role == null) {
      return false;
    }
    list.remove(role);
    return true;
  }

  public boolean existsById(int id) {
    return findById(id) != null;
  }
}
