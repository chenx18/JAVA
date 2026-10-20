package com.example.demo;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
  private final RoleRepository roleRepository;

  public RoleService(RoleRepository r) {
    this.roleRepository = r;
  }

  public List<Role> getAllRole() {
    return roleRepository.findAll();
  }

  public Role getRoleById(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("role id must be > 0");
    }
    Role r = roleRepository.findById(id);
    if (r == null) {
      throw new RoleNotFoundException(id);
    }
    return r;
  }

  public Role addRole(Role r) {
    if (r == null) {
      throw new IllegalArgumentException("role is null");
    }
    if (r.getId() <= 0) {
      throw new IllegalArgumentException("role id must be > 0");
    }
    if (r.getName() == null || r.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("role name is blank");
    }
    if (roleRepository.existsById(r.getId())) {
      throw new IllegalArgumentException("duplicate id: " + r.getId());
    }
    roleRepository.save(r);
    return r;
  }
}