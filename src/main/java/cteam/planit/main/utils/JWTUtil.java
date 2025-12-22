package cteam.planit.main.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cteam.planit.main.dao.UserRepository;
import cteam.planit.main.dto.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {
  @Value("${spring.security.jwt.secret}")
  String jwtSecret;
  @Value("${spring.security.jwt.expires}")
  Integer jwtExpires;
  @Value("${spring.security.jwt.expires.refresh}")
  Integer jwtExpiresRefresh;
  @Value("${spring.security.jwt.dummy.length}")
  Integer jwtDummyLength;

  @Autowired
  RandomUtil randomUtil;

  @Autowired
  UserRepository userRep;

  SecretKey key;

  @PostConstruct
  public void init() {
    key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  private String createToken(
      Map<String, Object> claims,
      String subject
  ) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(jwtExpires, ChronoUnit.SECONDS)))
        .signWith(key)
        .compact();
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put("username", user.getUsername());
    claims.put("email", user.getEmail());
    claims.put("dummy", randomUtil.getString(jwtDummyLength));
    return createToken(claims, user.getUsername());
  }

  private Claims extractToken(String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private Boolean expiredToken(Claims claims) {
    return claims.getExpiration().before(new Date());
  }

  public Boolean expiredHourToken(String token) {
    return extractToken(token)
      .getExpiration()
      .after(Date.from(Instant.now().minus(jwtExpiresRefresh, ChronoUnit.SECONDS)));
  }

  public Boolean verifyToken(String token) {
    try { return !expiredToken(extractToken(token)); }
    catch(Exception e) { return false; }
  }

  public Optional<User> tokenToUser(String token) {
    String username = extractToken(token).getSubject();
    return userRep.findByUsernameIgnoreCase(username);
  }
}
