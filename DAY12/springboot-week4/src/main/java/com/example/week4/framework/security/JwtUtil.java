package com.example.week4.framework.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  private static final String SECRET = "week4-jwt-secret-key-must-be-at-least-32-bytes";
  private static final long EXPIRE_TIME = 1000 * 60 * 60 * 2;

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Integer userId, String userName) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("userName", userName)
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(getSignKey())
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}