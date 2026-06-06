package com.example.week4.project.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.week4.project.system.domain.User;


@Mapper
public interface UserMapper {
  
  
  long count(@Param("user") User user);
  
  // 查询全部用户
  List<User> findAll();
 
  // 分页查询用户
  List<User> findPage(
    @Param("user") User user,
    @Param("offset") int offSet, 
    @Param("pageSize") int pageSize
  );

  // 新增用户
  int insert(@Param("user") User user);

  // 更新用户
  int updateById(@Param("id") int id, @Param("user") User user);

  // 删除用户
  int deleteById(@Param("id") int id);

  int deleteByIds(@Param("ids") List<Integer> ids);
  
}
