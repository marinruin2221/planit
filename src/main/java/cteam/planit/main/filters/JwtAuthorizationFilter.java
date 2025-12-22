package cteam.planit.main.filters;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cteam.planit.main.dto.User;
import cteam.planit.main.utils.CookieUtil;
import cteam.planit.main.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  @Autowired
  JWTUtil jwtUtil;
  @Autowired
  CookieUtil cookieUtil;

  @Value("${spring.security.jwt.name}")
  String jwtName;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    
      Cookie[] cookies = request.getCookies();
      if(cookies == null) {
        filterChain.doFilter(request, response);
        return;
      }

      String jwtToken = "";
      for(Cookie cookie : cookies) {
        if(cookie.getName().equals(jwtName)) {
          jwtToken = cookie.getValue();
          break;
        }
      }

      if(!StringUtils.hasText(jwtToken)) {
        cookieUtil.RemoveJwt(response);
        filterChain.doFilter(request, response);
        return;
      }

      if(!jwtUtil.verifyToken(jwtToken)) {
        cookieUtil.RemoveJwt(response);
        filterChain.doFilter(request, response);
        return;
      }

      Optional<User> ouser = jwtUtil.tokenToUser(jwtToken);

      if(ouser.isEmpty()) {
        cookieUtil.RemoveJwt(response);
        filterChain.doFilter(request, response);
        return;
      }

      User user = ouser.get(); 
      
      if(jwtUtil.expiredHourToken(jwtToken)) {
        cookieUtil.ApplyJwt(jwtUtil.generateToken(user), response);
      }
      
      UsernamePasswordAuthenticationToken upat = 
        new UsernamePasswordAuthenticationToken(
          user, null, user.getAuthorities()
        );

      SecurityContextHolder
        .getContext()
        .setAuthentication(upat);

      filterChain.doFilter(request, response);
  }
  
}
