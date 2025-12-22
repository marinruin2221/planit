package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.UserRepository;
import com.example.demo.dto.User;
import com.example.demo.utils.CookieUtil;
import com.example.demo.utils.JWTUtil;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SystemController {

  @Autowired
  UserRepository userRep;
  @Autowired
  CookieUtil cookieUtil;
  @Autowired
  JWTUtil jwtUtil;

  @Autowired
  PasswordEncoder encoder;

  @PostMapping("/signup")
  public String signup2(
      @RequestParam("username") String username,
      @RequestParam("password") String password,
      @RequestParam("email") String email,
      HttpServletResponse res) {
    Optional<User> user = userRep.findByUsernameIgnoreCase(username);
    if (user.isPresent() || username.contains("::"))
      return "redirect:/signup";
    User target = new User(null, username, encoder.encode(password), username, null, List.of("user"), email);
    userRep.save(target);
    cookieUtil.ApplyJwt(jwtUtil.generateToken(target), res);
    return "redirect:/home";
  }
}
