package com.example.week3;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository {
  private final List<Department> list = new ArrayList<>();

  public DepartmentRepository() {
    list.add(new Department(1, "超级组织"));
  }

  public List<Department> findAll() {
    return list;
  }

  public Department findById(int id) {
    for(Department d : list) {
      if(d.getId() == id) {
        return d;
      }
    }
    return null;
  }

  public Department save(Department d) {
    Department department = findById(d.getId());
    if(department != null) {
      return null;
    }

    list.add(d);
    return d;
  }

  public Department updateById(int id, Department d) {
    Department department = findById(id);
    if(department == null) {
      return null;
    }
    department.setName(d.getName());
    return department;
  }

  public boolean deleteById(int id) {
    Department department = findById(id);
    if(department == null) {
      return false;
    }

    list.remove(department);
    return true;
  }

  public boolean existsById(int id) {
    return findById(id) != null;
  }
  
}
