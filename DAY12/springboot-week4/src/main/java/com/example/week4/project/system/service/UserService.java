package com.example.week4.project.system.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.example.week4.common.response.PageResponse;
import com.example.week4.project.system.domain.User;
import com.example.week4.project.system.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper mapper;

  public List<User> getUserAllList() {
    return mapper.findAll();
  }

  public PageResponse<User> getUsersList(User request) {
    
    String name = request.getUserName();
    int pageSize = request.getPageSize();
    int pageNum = request.getPageNum();
    int offSet = (pageNum - 1) * pageSize;

    long total = mapper.findAll().size();
    List<User> users= mapper.count(request);

    return new PageResponse<>(total, users);
  }

  public void createUser(User request) {
    if(request == null) {
      throw new IllegalArgumentException("User is null");
    }
    int rows = mapper.insert(request);
    if(rows == 0) {
      throw new IllegalArgumentException("User add error");
    }
  }

  public void updateUser(int id, User request) {
     if(request == null) {
      throw new IllegalArgumentException("User is null");
    }
    int rows = mapper.updateById(id, request);
    if(rows == 0) {
      throw new IllegalArgumentException("User Update Error");
    }
  }

  public boolean deleteByIds(List<Integer> ids) {
     if(ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("ids cannot be empty");
    }

    for(Integer id : ids) {
      if(id== null || id <= 0) {
        throw new IllegalArgumentException("id must > 0");
      }
    }
    boolean deleted = mapper.deleteByIds(ids);
    if(!deleted) {
      throw new IllegalArgumentException("Delete delet Error");
    }
    return deleted;
  }
}
