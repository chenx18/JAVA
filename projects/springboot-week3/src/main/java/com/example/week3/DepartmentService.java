package com.example.week3;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DepartmentService {
  
  private final DepartmentRepository repository;

  public DepartmentService(DepartmentRepository repoistory) {
    this.repository = repoistory;
  }

  public List<Department> getAllDepartments() {
    return repository.findAll();
  }

  public Department getDepartmentById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Id must > 0");
    }
    Department d = repository.findById(id);
    if(d == null) {
      throw new DepartmentNotFoundException(id);
    }
    return d;
  }

  public Department createDepartment(Department d) {
    if(d == null) {
      throw new IllegalArgumentException("Department is null");
    }

    if(d.getName() == null || d.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Department nam is blank");
    }

    if(d.getId() <= 0) {
      throw new IllegalArgumentException("Department id must > 0");
    }

    if(repository.existsById(d.getId())) {
      throw new IllegalArgumentException("Department already exists: " + d.getId());
    }

    repository.save(d);
    return d;
  }

  public Department updateDepartment(int id, Department d) {
    if(id <= 0) {
      throw new IllegalArgumentException("Id must > 0");
    }
    if(d == null) {
      throw new IllegalArgumentException("Department is null");
    }

    if(d.getName() == null || d.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Department nam is blank");
    }
    Department department = repository.updateById(id, d);
    if(department == null){
      throw new DepartmentNotFoundException(id);
    }
    return department;
  }

  public void deleteDepartment(int id) {
     if(id <= 0) {
      throw new IllegalArgumentException("Id must > 0");
    }
    boolean deleted = repository.deleteById(id);
    if(!deleted) {
      throw new DepartmentNotFoundException(id);
    }
  }

}
