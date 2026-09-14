package com.example.week4.project.system.service;

import java.util.List;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.week4.common.response.PageResponse;
import com.example.week4.framework.security.JwtUtil;
import com.example.week4.project.system.domain.User;
import com.example.week4.project.system.domain.request.LoginRequest;
import com.example.week4.project.system.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper mapper;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  public List<User> getUserAllList() {
    return mapper.findAll();
  }

  public PageResponse<User> getUsersList(User request) {
    
    int pageSize = request.getPageSize();
    int pageNum = request.getPageNum();
    int offSet = (pageNum - 1) * pageSize;

    long total = mapper.count(request);
    List<User> users= mapper.findPage(request, offSet, pageSize);

    return new PageResponse<>(total, users);
  }

  public void createUser(User request) {
    if(request == null) {
      throw new IllegalArgumentException("User is null");
    }

    if(request.getPassword() == null || request.getPassword().isBlank()){
      throw new IllegalArgumentException("Password is blank");
    }

    request.setPassword(passwordEncoder.encode(request.getPassword()));

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

  public void deleteByIds(List<Integer> ids) {
     if(ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("ids cannot be empty");
    }

    for(Integer id : ids) {
      if(id== null || id <= 0) {
        throw new IllegalArgumentException("id must > 0");
      }
    }
    int rows = mapper.deleteByIds(ids);
    if(rows == 0) {
      throw new IllegalArgumentException("Delete delet Error");
    }
  }


  public String login(LoginRequest request) {
    if(request == null) {
      throw new IllegalArgumentException("Login fail");
    }
    if(request.getUserName() == null || request.getUserName().trim().isEmpty()){
      throw new IllegalArgumentException("Login fail");
    }
    if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
      throw new IllegalArgumentException("Login fail");
    }

    User user = mapper.findByUserName(request.getUserName());

    if(user == null) {
      throw new IllegalArgumentException("Username or password error");
    }

    if(passwordEncoder.matches(request.getPassword(), user.getPassword())){
      throw new IllegalArgumentException("Username or password error");
    }

    return jwtUtil.generateToken(user.getId(), user.getUserName());
  }
}
