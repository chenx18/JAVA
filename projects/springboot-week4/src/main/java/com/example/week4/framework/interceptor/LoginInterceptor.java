package com.example.week4.framework.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.week4.framework.security.JwtUtil;
import com.example.week4.framework.security.LoginUser;
import com.example.week4.framework.security.LoginUserContext;

import io.jsonwebtoken.Claims;

@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {
  
  private final JwtUtil jwtUtil;

  @Override
  public boolean preHandle(
    HttpServletRequest request, 
    HttpServletResponse response, 
    Object handler
  ) throws Exception {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (token == null || token.isBlank()) {
      writeUnauthorized(response, "please login");
      return false;
    }

    try {
      Claims claims = jwtUtil.parseToken(token);
      Integer userId = Integer.valueOf(claims.getSubject());
      String userName = claims.get("userName", String.class);
      
      LoginUserContext.set(new LoginUser(userId, userName));
      return true;
    } catch (Exception e) {
      writeUnauthorized(response, "invalid token");
      return false;
    }
  }

  private void writeUnauthorized(
    HttpServletResponse response, 
    String message
  ) throws Exception {
    response.setStatus(401);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
  }

  @Override
  public void afterCompletion(
    HttpServletRequest request, 
    HttpServletResponse response, 
    Object handler, 
    Exception ex
  ){
    LoginUserContext.remove();
  }
}