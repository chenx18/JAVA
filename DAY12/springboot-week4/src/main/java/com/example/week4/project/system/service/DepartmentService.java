package com.example.week4.project.system.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.week4.common.enums.DepartmentStatusEnum;
import com.example.week4.common.exception.DepartmentNotFoundException;
import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.Department;
import com.example.week4.project.system.domain.DepartmentLog;
import com.example.week4.project.system.domain.request.DepartmentCreateRequest;
import com.example.week4.project.system.domain.request.DepartmentQueryRequest;
import com.example.week4.project.system.domain.request.DepartmentUpdateRequest;
import com.example.week4.project.system.domain.vo.DepartmentDetailResponse;
import com.example.week4.project.system.domain.vo.DepartmentResponse;
import com.example.week4.project.system.mapper.DepartmentLogMapper;
import com.example.week4.project.system.mapper.DepartmentMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentService {

  private final DepartmentMapper mapper;
  private final DepartmentLogMapper logMapper;

  public PageResponse<DepartmentResponse> getDepartmentPages(DepartmentQueryRequest request) {
    int pageNum = request.getPageNum();
    int pageSize = request.getPageSize();
    String name = request.getName();

    int offset = (pageNum - 1) * pageSize;
    List<DepartmentResponse> rows = mapper.findPage(name, offset, pageSize)
        .stream()
        .map(this::toResponse)
        .toList();

    long total = mapper.countQuery(name);
    return new PageResponse<>(total, rows);
  }

  public List<DepartmentResponse> getAllDepartments() {
    return mapper.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
  }

  public DepartmentResponse getDepartmentById(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    Department department = mapper.findById(id);
    if (department == null) {
      throw new DepartmentNotFoundException(id);
    }

    return toResponse(department);
  }

  public DepartmentResponse getDepartmentByName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }

    Department department = mapper.findByName(name);
    if (department == null) {
      throw new DepartmentNotFoundException(name);
    }

    return toResponse(department);
  }

  public List<DepartmentResponse> searchDepartmentsByName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }

    return mapper.searchByName(name)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("department request cannot be null");
    }

    if (mapper.existsByName(request.getName())) {
      throw new IllegalArgumentException("department name already exists");
    }

    validateStatus(request.getStatus());

    Department department = new Department();
    department.setName(request.getName());
    department.setDescription(request.getDescription());
    department.setStatus(request.getStatus());

    mapper.insert(department);

    saveDepartmentLog(
      department.getId(),
      "CREATE_DEPARTMENT",
      "Create department: " + department.getName()
    );

    return toResponse(department);
  }

  @Transactional
  public DepartmentResponse updateDepartment(int id, DepartmentUpdateRequest request) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }
    if (request == null) {
      throw new IllegalArgumentException("department request cannot be null");
    }

    if (request.getName() == null && request.getDescription() == null && request.getStatus() == null) {
      throw new IllegalArgumentException("no field to update");
    }

    if (!mapper.existsById(id)) {
      throw new DepartmentNotFoundException(id);
    }

    if (request.getName() != null && !request.getName().isBlank()) {
      Department sameNameDepartment = mapper.findByName(request.getName());
      if (sameNameDepartment != null && sameNameDepartment.getId() != id) {
        throw new IllegalArgumentException("department name already exists");
      }
    }

    validateStatus(request.getStatus());

    Department department = new Department();
    department.setId(id);
    department.setName(request.getName());
    department.setDescription(request.getDescription());
    department.setStatus(request.getStatus());

    int rows = mapper.updateById(department);
    if (rows == 0) {
      throw new DepartmentNotFoundException(id);
    }

    Department updated = mapper.findById(id);

    saveDepartmentLog(
      department.getId(),
      "UPDATE_DEPARTMENT",
      "Update department: " + department.getName()
    );

    return toResponse(updated);
  }

  @Transactional
  public void deleteDepartment(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    Department department = mapper.findById(id);
    if (department == null) {
      throw new DepartmentNotFoundException(id);
    }

    int rows = mapper.deleteById(id);
    if (rows == 0) {
      throw new DepartmentNotFoundException(id);
    }

    saveDepartmentLog(
      id,
      "DELETE_DEPARTMENT",
      "Delete department: " + department.getName());
  }

  @Transactional
  public void batchDeleteDepartments(List<Integer> ids) {
    if(ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("ids cannot be empty");
    }

    for(Integer id : ids) {
      if(id== null || id <= 0) {
        throw new IllegalArgumentException("id must > 0");
      }
    }

    List<Department> departments = mapper.findByIds(ids);
    for(Department department : departments) {
      saveDepartmentLog(
        department.getId(), 
        "DELETE_DEPARTMENT", 
        "Delete department" + department.getName()
      );
    }

    int rows = mapper.deleteByIds(ids);
    if(rows == 0) {
      throw new IllegalArgumentException("no department deleted");
    }
  }

  private void validateStatus(Integer status) {
    if (status == null) {
      return;
    }
    if (!DepartmentStatusEnum.containsCode(status)) {
      throw new IllegalArgumentException("status must be 0 or 1");
    }
  }

  private DepartmentResponse toResponse(Department department) {
    DepartmentResponse response = new DepartmentResponse();
    response.setId(department.getId());
    response.setName(department.getName());
    response.setDescription(department.getDescription());
    response.setStatus(department.getStatus());
    return response;
  }

  private void saveDepartmentLog(Integer departmentId, String operation, String content) {
    DepartmentLog log = new DepartmentLog();
    log.setDepartmentId(departmentId);
    log.setOperation(operation);
    log.setContent(content);
    log.setCreateTime(LocalDateTime.now());

    logMapper.insert(log);
  }

  public List<DepartmentLog> getDepartmentLogs(int departmentId) {
    if(departmentId <= 0) {
      throw new IllegalArgumentException("dapartment id must > 0");
    }
    if(!mapper.existsById(departmentId)) {
      throw new DepartmentNotFoundException(departmentId);
    }

    return logMapper.findByDepartmentId(departmentId);
  }

  public DepartmentDetailResponse getDepartmentDetails(int departmentId) {
    if(departmentId <= 0) {
      throw new IllegalArgumentException("dapartment id must > 0");
    }
    
    DepartmentDetailResponse detail = mapper.findDetailById(departmentId);

    if(detail == null) {
      throw new DepartmentNotFoundException(departmentId);
    }

    return detail;
  }
}
