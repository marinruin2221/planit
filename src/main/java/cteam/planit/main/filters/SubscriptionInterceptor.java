package cteam.planit.main.filters;

import java.nio.file.AccessDeniedException;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import cteam.planit.main.dto.User;

import jakarta.annotation.PostConstruct;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

  @Value("${spring.security.websocket.config.auth.paths}")
  String authPath;
  @Value("${spring.security.websocket.config.admin.paths}")
  String adminPath;

  String[] authPaths;
  String[] adminPaths;

  @PostConstruct
  public void Construct() {
    authPaths = authPath.split(",");
    adminPaths = adminPath.split(",");
  }

  @Override
  public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex) {
    // ë©”ì‹œì§€ë¥??±ê³µ?ìœ¼ë¡??˜ì‹ ?????¡ì…˜
    ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
  }

  @Override
  public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
    // ë©”ì‹œì§€ë¥??±ê³µ?ìœ¼ë¡??„ë‹¬?????¡ì…˜
    ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
  }

  @Override
  public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
    try {
      if (MessageHeaderAccessor.getAccessor(message) instanceof StompHeaderAccessor acc) {
        if (StompCommand.SUBSCRIBE.equals(acc.getCommand())) {

          String path = acc.getDestination();
          User user = (User) acc.getUser();
          for (String adminPath : adminPaths) {
            if (adminPath.equalsIgnoreCase(path)) {
              if (user == null)
                throw new AccessDeniedException("Subscribe ê¶Œí•œ ë¶€ì¡?);
              if (user.roles.contains("admin"))
                return message;
              else
                throw new AccessDeniedException("Subscribe ê¶Œí•œ ë¶€ì¡?);
            }
          }
          for (String authPath : authPaths) {
            if (authPath.equalsIgnoreCase(path)) {
              if (user == null)
                throw new AccessDeniedException("Subscribe ê¶Œí•œ ë¶€ì¡?);
              if (user.roles.contains("admin") || user.roles.contains("user"))
                return message;
              else
                throw new AccessDeniedException("Subscribe ê¶Œí•œ ë¶€ì¡?);
            }
          }

        }
        return message;
      }
    } catch (AccessDeniedException e) {
    }
    return null;
  }

}
