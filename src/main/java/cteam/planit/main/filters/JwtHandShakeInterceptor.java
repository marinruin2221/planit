package cteam.planit.main.filters;

import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cteam.planit.main.dto.User;
import cteam.planit.main.utils.CookieUtil;
import cteam.planit.main.utils.JWTUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

  @Value("${spring.security.jwt.name}")
  String jwtName;
  Boolean websocketJwtOnly;

  @Autowired
  CookieUtil cookieUtil;
  @Autowired
  JWTUtil jwtUtil;

  @Override
  public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
      @Nullable Exception arg3) {
    // ?∏Îìú?∞Ïù¥???¥ÌõÑ ?ôÏûë, Ï¶??±Í≥µ ??Î¨¥Ïä®?ºÏùÑ ?†Ï? ?ëÏÑ±?òÎäî Í∏∞Îä•
    // ?¨Í∏∞???ºÎ∞ò?ÅÏúºÎ°?DBMS??Î°úÍ∑∏Î•??®Í∏¥?§Í±∞???†ÎïåÎß??∞Ïù∏??
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    if(req instanceof ServletServerHttpRequest servletRequest) {
      if(res instanceof ServletServerHttpResponse servletResponse) {
        HttpServletRequest request = servletRequest.getServletRequest();
        HttpServletResponse response = servletResponse.getServletResponse();
    
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return !websocketJwtOnly;

        String jwtToken = "";
        for(Cookie cookie : cookies) {
          if(cookie.getName().equals(jwtName)) {
            jwtToken = cookie.getValue();
            break;
          }
        }

        if(!StringUtils.hasText(jwtToken)) {
          cookieUtil.RemoveJwt(response);
          return !websocketJwtOnly;
        }

        if(!jwtUtil.verifyToken(jwtToken)) {
          cookieUtil.RemoveJwt(response);
          return !websocketJwtOnly;
        }

        Optional<User> ouser = jwtUtil.tokenToUser(jwtToken);

        if(ouser.isEmpty()) {
          cookieUtil.RemoveJwt(response);
          return !websocketJwtOnly;
        }
        User user = ouser.get(); 
        
        if(jwtUtil.expiredHourToken(jwtToken)) 
          cookieUtil.ApplyJwt(jwtUtil.generateToken(user), response);

        attributes.put("Principal", user);

        return true;
      }
    }

    return !websocketJwtOnly;
  }
}
