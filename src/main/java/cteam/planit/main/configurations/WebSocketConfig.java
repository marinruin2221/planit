package cteam.planit.main.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import cteam.planit.main.filters.JwtHandShakeHandler;
import cteam.planit.main.filters.JwtHandShakeInterceptor;
import cteam.planit.main.filters.SubscriptionInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired
  JwtHandShakeInterceptor jwtInterceptor;
  @Autowired
  JwtHandShakeHandler jwtHandler;
  @Autowired
  SubscriptionInterceptor subscriptionInterceptor;

  @Value("${spring.websocket.channels}")
  String websocketChannels;
  @Value("${spring.websocket.prefix.user}")
  String websocketUserPrefix;
  @Value("${spring.websocket.prefix.app}")
  String websocketAppPrefix;
  @Value("${spring.websocket.endpoint}")
  String websocketEndpoint;
  @Value("${spring.websocket.allowed-patterns}")
  String websocketAllowedPatterns;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(subscriptionInterceptor);
    WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(websocketChannels.split(","));
    registry.setApplicationDestinationPrefixes(websocketAppPrefix);
    registry.setUserDestinationPrefix(websocketUserPrefix);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(websocketEndpoint)
        .setAllowedOriginPatterns(websocketAllowedPatterns.split(","))
        .addInterceptors(jwtInterceptor)
        .setHandshakeHandler(jwtHandler)
        .withSockJS()
        .setSessionCookieNeeded(true);
  }

}

