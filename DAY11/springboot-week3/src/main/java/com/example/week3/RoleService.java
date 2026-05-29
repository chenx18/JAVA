package com.example.week3;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
  
  private final RoleRepository repository;

  public RoleService(RoleRepository r) {
    this.repository = r;
  }

  public List<Role> getAllRoles() {
    return repository.findAll();
  }

  public Role getRoleById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Role id must > 0");
    }

    Role role = repository.findById(id);
    if(role == null) {
      throw new RoleNotFoundException(id);
    }

    return role;
  }

  public Role createRole(Role r){
    if(r == null) {
      throw new IllegalArgumentException("Role is null");
    }
    
    if(r.getName() == null || r.getName().isEmpty()) {
      throw new IllegalArgumentException("Role name is black");
    }

    if(r.getId() <= 0) {
      throw new IllegalArgumentException("Role id must > 0");
    }

    if(repository.existsById(r.getId())){
      throw new IllegalArgumentException("role already exists:" + r.getId());
    }

    repository.saveRole(r);
    return r;
  }

  public Role updateRole(int id, Role r){

    if(id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    if(r == null) {
      throw new IllegalArgumentException("Role is null");
    }

    if(r.getName() == null || r.getName().isEmpty()) {
      throw new IllegalArgumentException("Role name is balck");
    }

    Role role = repository.updateRoleById(id, r);
    if(role == null) {
      throw new RoleNotFoundException(id);
    }
    return role;
  }

  public void deleteRole(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Role id must > 0");
    }

    boolean deleted = repository.deleteRoleById(id);
    if(!deleted) {
      throw new RoleNotFoundException(id);
    }
  }

}
