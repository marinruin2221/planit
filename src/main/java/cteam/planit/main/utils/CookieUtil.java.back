package cteam.planit.main.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
  @Value("${spring.security.jwt.expires}")
  Integer jwtExpires;
  @Value("${spring.security.jwt.name}")
  String jwtName;
  @Value("${spring.security.jwt.domain}")
  String jwtDomain;
  @Value("${spring.security.jwt.secure}")
  Boolean jwtSecure;
  @Value("${spring.security.cookie.http-only}")
  Boolean cookieHttpOnly;
  @Value("${spring.security.cookie.secure}")
  Boolean cookieSecure;

  public void CookieAdd(
    String name, String value, Integer expires, String domain, Boolean secure,
    HttpServletResponse res
  ) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(cookieHttpOnly);
    cookie.setPath("/");
    cookie.setMaxAge(expires);
    cookie.setDomain(domain);
    cookie.setSecure(cookieSecure);
    res.addCookie(cookie);
  }

  public void ApplyJwt(String jwt, HttpServletResponse res) {
    CookieAdd(jwtName, jwt, jwtExpires, jwtDomain, jwtSecure, res);
  }
  public void RemoveJwt(HttpServletResponse res) {
    CookieAdd(jwtName, "", 0, jwtDomain, jwtSecure, res);
  }
}

