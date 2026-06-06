package com.example.week4.project.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.week4.project.system.domain.DictData;

@Mapper
public interface DictMapper {
  
  long count(@Param("d") DictData d);

  List<DictData> findPage(
    @Param("d") DictData d,
    @Param("offset") Integer offset,
    @Param("pageSize") Integer pageSize
  );

  DictData findById(@Param("id") Integer id);

  int insert(@Param("d") DictData d);

  int updateById(@Param("id") Integer id, @Param("d") DictData D);

  int deleteById(@Param("id") Integer ids);
  
  int deleteByIds(@Param("ids") List<Integer> ids);

}
