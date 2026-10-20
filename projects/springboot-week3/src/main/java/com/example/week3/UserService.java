package com.example.week3;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository repository;

  public UserService(UserRepository r) {
    this.repository = r;
  }

  public List<User> getAllUsers() {
    return repository.findAll();
  }


  public User getUserById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    User user = repository.findById(id);
    if(user == null) {
      throw new UserNotFoundException(id);
    }
    return user;
  }

  public User createUser(User u){
    if(u == null) {
      throw new IllegalArgumentException("USER IS NULL");
    }
    if(u.getName() == null || u.getName().isEmpty()){
      throw new IllegalArgumentException("USER NAME IS BLACK");
    }

    if(u.getId() <= 0) {
      throw new IllegalArgumentException("USER ID <= 0");
    }

    if(repository.existsById(u.getId())) {
      throw new IllegalArgumentException("ID IS HAS");
    }

    repository.save(u);
    return u;
  }

  public User updateUser(int id, User u){
    if(id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    if(u == null) {
      throw new IllegalArgumentException("USER IS NULL");
    }

    if(u.getName() == null || u.getName().isEmpty()) {
      throw new IllegalArgumentException("user name null");
    }

    User user = repository.updateById(id, u);
    if(user == null) {
      throw new UserNotFoundException(id);
    }
    return user;
  }

  public void deleteUser(int id) {
    if(id < 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    boolean del = repository.deleteById(id);
    if(!del) {
      throw new UserNotFoundException(id);
    }
  }


}