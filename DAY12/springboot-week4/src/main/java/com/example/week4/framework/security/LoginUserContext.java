package com.example.week4.framework.security;

public class LoginUserContext {

  private static final ThreadLocal<LoginUser> LOCAL = new ThreadLocal<>();
  
  public static void set(LoginUser loginUser) {
    LOCAL.set(loginUser);
  }

  public static LoginUser get() {
    return LOCAL.get();
  }

  public static Integer getUserId() {
    LoginUser loginUser = get();
    return loginUser == null ? null : loginUser.getUserId();
  }

  public static String getUserName() {
    LoginUser loginUser = get();
    return  loginUser == null ? null : loginUser.getUserName();
  }

  public static void remove() {
    LOCAL.remove();
  }
}
