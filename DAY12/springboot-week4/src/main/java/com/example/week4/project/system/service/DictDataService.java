package com.example.week4.project.system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.DictData;
import com.example.week4.project.system.mapper.DictMapper;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class DictDataService {
  
  private final DictMapper mapper;

  public PageResponse<DictData> getDictPages(DictData d){

    int pageNum = d.getPageNum();
    int pageSize = d.getPageSize();
    int offSet = (pageNum - 1) * pageSize;
    long total = mapper.count(d);
    List<DictData> dict = mapper.findPage(d, offSet, pageSize);

    return new PageResponse<>(total, dict);
  }

  public void createDicData(DictData d){
    if(d == null) {
      throw new IllegalArgumentException("Dict is null");
    }
    if(d.getDictLabel() == null || d.getDictLabel().isBlank()) {
      throw new IllegalArgumentException("Dict label is blank");
    }
    if(d.getDictValue() == null || d.getDictValue().isBlank()) {
      throw new IllegalArgumentException("Dict value is blank");
    }

    int rows = mapper.insert(d);

    if(rows == 0) {
      throw new IllegalArgumentException("Dict create error");
    }
  }

  public void updateById(Integer id, DictData d) {
    if(id == null || id <= 0) {
      throw new IllegalArgumentException("Dict id must > 0");
    }
    if(d == null) {
      throw new IllegalArgumentException("Dict is null");
    }

    int rows = mapper.updateById(id, d);

    if(rows == 0) {
      throw new IllegalArgumentException("Dict update error");
    }
  }

  public void deleteById(Integer id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Dict id must > 0");
    }
    int rows = mapper.deleteById(id);
    if(rows == 0) {
      throw new IllegalArgumentException("Delete Dict id=" + id + " error");
    }
  }

  public void deleteByIds(List<Integer> ids) {
    if(ids == null) {
      throw new IllegalArgumentException("Delete Dicr ids null");
    }
    for (Integer id : ids) {
      if(id <= 0 || id == null) {
        throw new IllegalArgumentException("Delete Dicr id:" + id +" error");
      }
    }

    int rows = mapper.deleteByIds(ids);
    if(rows == 0) {
      throw new IllegalArgumentException("Delete Dict ids" + ids + "error");
    }
  }
}
