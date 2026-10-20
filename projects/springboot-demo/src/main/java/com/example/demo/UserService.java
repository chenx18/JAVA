package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository r) {
    this.userRepository = r;
  }

  public User getUserById(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }

    User u = userRepository.findById(id);
    if (u == null) {
      throw new UserNotFoundException(id);
    }

    return u;
  }

  public User addUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("user is null");
    }
    if (user.getId() <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }
    if (user.getName() == null || user.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("user name is blank");
    }
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("email is blank");
    }
    if (userRepository.existsById(user.getId())) {
      throw new IllegalArgumentException("duplicate id: " + user.getId());
    }

    userRepository.save(user);
    return user;
  }
}