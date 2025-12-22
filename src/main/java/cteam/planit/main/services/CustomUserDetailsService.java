package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserRepository;
import com.example.demo.dto.User;
import com.example.demo.utils.RandomUtil;

@Service
public class CustomUserDetailsService extends DefaultOAuth2UserService implements UserDetailsService {

  @Autowired
  UserRepository userRep;

  @Autowired
  RandomUtil randomUtil;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRep.findByUsernameIgnoreCase(username);
    if (user.isEmpty())
      throw new UsernameNotFoundException(username);
    if (user.get().password == null)
      throw new UsernameNotFoundException(username);
    return user.get();
  }

  // Naver, Kakao 직접 제작
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User ouser = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getClientName();
    String identified = ouser.getAttributes().get("id").toString();
    String username = provider + "::" + identified;
    Optional<User> fuser = userRep.findByUsernameIgnoreCase(username);
    User target;
    if (fuser.isEmpty()) {
      target = userRep.save(
          new User(
              null,
              username,
              null,
              "Guest_" + randomUtil.gerInteger(10000000, 99999999),
              null,
              List.of("user"), ""));
    } else
      target = fuser.get();
    return target;
  }
}
