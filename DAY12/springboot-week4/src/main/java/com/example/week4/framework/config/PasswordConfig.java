package com.example.week4.framework.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable
public class PasswordConfig {
  
  @Bean
  public PasswordEncoder passwordConfig() {
    return new BCryptPasswordEncoder();
  }
  
}
