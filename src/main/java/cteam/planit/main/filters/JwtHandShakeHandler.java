package com.example.demo.filters;

import java.security.Principal;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.demo.dto.User;

@Component
public class JwtHandShakeHandler extends DefaultHandshakeHandler {
  
  @Override
  protected @Nullable Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
      Map<String, Object> attributes) {
    User user = (User)attributes.get("Principal");
    if(user == null) return super.determineUser(request, wsHandler, attributes);
    return user;
  }
}
