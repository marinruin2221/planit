package cteam.planit.main.configurations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cteam.planit.main.dto.User;
import cteam.planit.main.filters.JwtAuthorizationFilter;
import cteam.planit.main.services.CustomUserDetailsService;
import cteam.planit.main.utils.CookieUtil;
import cteam.planit.main.utils.JWTUtil;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  CustomUserDetailsService cuds;

  @Autowired
  CookieUtil cookieUtil;
  @Autowired
  JWTUtil jwtUtil;
  @Autowired
  JwtAuthorizationFilter jwtFilter;

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Value("${spring.security.config.csrf.paths}")
  String csrfPaths;
  @Value("${spring.security.config.auth.paths}")
  String authPaths;
  @Value("${spring.security.config.admin.paths}")
  String adminPaths;
  @Value("${spring.security.config.cookies}")
  String cookies;
  @Value("${spring.security.config.login.page}")
  String loginPage;
  @Value("${spring.security.config.login.failure}")
  String loginFailiure;
  @Value("${spring.security.config.login.success}")
  String loginSuccess;
  @Value("${spring.security.config.login.logic}")
  String loginLogic;
  @Value("${spring.security.config.login.password}")
  String loginPasswordName;
  @Value("${spring.security.config.login.username}")
  String loginUsernameName;
  @Value("${spring.security.config.logout.success}")
  String logoutSuccess;
  @Value("${spring.security.config.logout.url}")
  String logoutUrl;
  @Value("${spring.security.config.rememberme.key}")
  String rmKey;
  @Value("${spring.security.config.rememberme.always}")
  Boolean rmAlways;
  @Value("${spring.security.config.rememberme.domain}")
  String rmdomain;
  @Value("${spring.security.config.rememberme.name}")
  String rmName;
  @Value("${spring.security.config.rememberme.validity}")
  Integer rmValidity;
  @Value("${spring.security.config.auth.fail.message}")
  String authFailMessage;
  @Value("${spring.security.config.auth.fail.code}")
  Integer authFailCode;
  @Value("${spring.security.config.cors.origins}")
  String corsAllowedOrigins;
  @Value("${spring.security.config.cors.methods}")
  String corsAllowedMethods;
  @Value("${spring.security.config.cors.headers}")
  String corsAllowedHeaders;

  public CorsConfigurationSource cors() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(corsAllowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of(corsAllowedMethods.split(",")));
    configuration.setAllowedHeaders(List.of(corsAllowedHeaders.split(",")));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(csrfPaths.split(",")))
        .cors(cors -> cors
            .configurationSource(cors()))
        .authorizeHttpRequests(req -> req
            .dispatcherTypeMatchers(DispatcherType.FORWARD)
            .permitAll()
            .requestMatchers(authPaths.split(","))
            .authenticated()
            .requestMatchers(adminPaths.split(","))
            .hasAnyRole("admin")
            .anyRequest()
            .permitAll())
        .formLogin(login -> login
            .loginPage(loginPage)
            .loginProcessingUrl(loginLogic)
            .successHandler((req, res, auth) -> {
              cookieUtil.ApplyJwt(
                  jwtUtil.generateToken((User) auth.getPrincipal()),
                  res);
              res.sendRedirect(loginSuccess);
            })
            .failureUrl(loginFailiure)
            .usernameParameter(loginUsernameName)
            .passwordParameter(loginPasswordName))
        .logout(logout -> logout
            .logoutUrl(logoutUrl)
            .logoutSuccessHandler((req, res, auth) -> {
              cookieUtil.RemoveJwt(res);
              res.sendRedirect(logoutSuccess);
            })
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .deleteCookies(cookies.split(",")))
        .rememberMe(rm -> rm
            .key(rmKey)
            .tokenValiditySeconds(rmValidity)
            .rememberMeCookieDomain(rmdomain)
            .rememberMeCookieName(rmName)
            .useSecureCookie(true)
            .alwaysRemember(rmAlways)
            .rememberMeParameter(rmName))
        .oauth2Login(login -> login
            .loginPage(loginPage)
            .successHandler((req, res, auth) -> {
              cookieUtil.ApplyJwt(
                  jwtUtil.generateToken((User) auth.getPrincipal()),
                  res);
              res.sendRedirect(loginSuccess);
            })
            .userInfoEndpoint(info -> info.userService(cuds))
            .failureUrl(loginFailiure))
        .userDetailsService(cuds)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(err -> err
            .authenticationEntryPoint((req, res, auth) -> {
              res.getWriter().write("{\"message\":\"" + authFailMessage + "\", \"code\":" + authFailCode + "}");
            }))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .getOrBuild();
  }
}
