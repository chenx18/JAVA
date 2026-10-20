package com.example.week4.project.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.week4.project.system.domain.Department;
import com.example.week4.project.system.domain.vo.DepartmentDetailResponse;

@Mapper
public interface DepartmentMapper {

  long countAll();

  List<Department> findAll();

  long countQuery(@Param("name") String name);

  List<Department> findPage(
    @Param("name") String name, 
    @Param("offset") int offset, 
    @Param("pageSize") int pageSize
  );

  Department findById(@Param("id") int id);

  Department findByName(@Param("name") String name);
  
  List<Department> findByIds(@Param("ids") List<Integer> ids);

  List<Department> searchByName(@Param("name") String name);

  DepartmentDetailResponse findDetailById(@Param("departmentId") int departmentId);

  int insert(Department department);

  int updateById(Department department);

  int deleteById(@Param("id") int id);

  int deleteByIds(@Param("ids") List<Integer> ids);

  boolean existsById(@Param("id") int id);

  boolean existsByName(@Param("name") String name);
}