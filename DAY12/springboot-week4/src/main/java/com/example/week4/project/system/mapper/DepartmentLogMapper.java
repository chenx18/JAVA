package com.example.week4.project.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.week4.project.system.domain.DepartmentLog;

@Mapper
public interface DepartmentLogMapper {

  int insert(DepartmentLog log);

  List<DepartmentLog> findByDepartmentId(@Param("departmentId") Integer departmentId);
}
