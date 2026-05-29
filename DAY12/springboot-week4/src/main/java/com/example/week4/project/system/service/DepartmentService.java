package com.example.week4.project.system.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.example.week4.common.exception.DepartmentNotFoundException;
import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.Department;
import com.example.week4.project.system.domain.request.DepartmentCreateRequest;
import com.example.week4.project.system.domain.request.DepartmentQueryRequest;
import com.example.week4.project.system.domain.request.DepartmentUpdateRequest;
import com.example.week4.project.system.domain.vo.DepartmentResponse;
import com.example.week4.project.system.mapper.DepartmentMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepartmentService {

  private final DepartmentMapper mapper;

  // RequiredArgsConstructor濞村吋淇洪崵婊堝礉閵娧勬櫢闁瑰瓨鍔掔粭鍛村棘闁稑鏁╅柣?  // public DepartmentService(DepartmentMapper mapper) {
  //   this.mapper = mapper;
  // }

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

  // mapper.findAll() 闁哄被鍎遍崵?List<Department>
  // stream() 闁硅泛锕ら崹顏嗘偘閵娿儱缍侀柟瀛樺姈缁侊箑顫濈€电娈?  // map(this::toResponse) 闁硅泛锕ラ惁鈩冪▔?Department 閺夌儐鍓氶崹?DepartmentResponse
  // toList() 闁告劕绉甸弫褰掓⒖閸℃ê鐏?List<DepartmentResponse>
  public List<DepartmentResponse> getAllDepartments() {
    return mapper.findAll()
    .stream()
    .map(this::toResponse)
    .toList();
  }

  public DepartmentResponse getDepartmentById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("department not found");
    }

    Department department = mapper.findById(id);
    if(department == null) {
      throw new DepartmentNotFoundException(id);
    }

    return toResponse(department);
  }

  public DepartmentResponse getDepartmentByName(String name) {
    if(name == null) {
      throw new IllegalArgumentException("Name is null");
    }
    
    Department department = mapper.findByName(name);
    if(department == null) {
      throw new DepartmentNotFoundException(name);
    }
    return toResponse(department);
  }

  public List<DepartmentResponse> searchDepartmentsByName(String name) {
    if(name == null) {
      throw new IllegalArgumentException("Name is null");
    }

    return mapper.searchByName(name)
      .stream()
      .map(this::toResponse)
      .toList();
  }

  public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
    if(request == null) {
      throw new IllegalArgumentException("Department is null");
    }

    if(mapper.existsByName(request.getName())) {
      throw new IllegalArgumentException("department name already exists");
    }

    validateStatus(request.getStatus());

    Department department = new Department();
    department.setName(request.getName());
    department.setDescription(request.getDescription());
    department.setStatus(request.getStatus());

    mapper.insert(department);
    return toResponse(department);
  }

  public DepartmentResponse updateDepartment(int id, DepartmentUpdateRequest request) {
    if(id <= 0) {
      throw new IllegalArgumentException("department not found");
    }
    if(request == null) {
      throw new IllegalArgumentException("Department is null");
    }

    if (
      request.getName() == null &&
      request.getDescription() == null &&
      request.getStatus() == null
    ) {
      throw new IllegalArgumentException("no field to update");
    }
    if(!mapper.existsById(id)) {
      throw new IllegalArgumentException("department not found");
    }

    if(request.getName() != null && !request.getName().isBlank()){
      Department sameNameDepartment = mapper.findByName(request.getName());
      if(sameNameDepartment != null && sameNameDepartment.getId() != id) {
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
    if(rows == 0) {
      throw new DepartmentNotFoundException(id);
    }

    return toResponse(department);
  }

  public void deleteDepartment(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }
    if(!mapper.existsById(id)){
      throw new IllegalArgumentException("閸掔娀娅庢径杈Е閿涘本婀幍鎯у煂" + id);
    }

    int rows = mapper.deleteById(id);

    if (rows == 0) {
      throw new DepartmentNotFoundException(id);
    }
  }

  public void validateStatus(Integer status) {
    if(status == null) {
      return;
    }
    if(status != 0 && status != 1) {
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


}