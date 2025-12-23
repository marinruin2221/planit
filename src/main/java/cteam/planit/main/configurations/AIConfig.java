package cteam.planit.main.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AIConfig {
  @Value("${spring.ai.model}")
  String model;
  @Value("${spring.ai.api}")
  String apiKey;

  @Bean(name = "gemini_generate_content")
  @Scope("singleton")
  public WebClient geminiGenerateContent() {
    return WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent")
        .defaultHeaders(headers -> {
          headers.add("Content-Type", "application/json");
          headers.add("X-goog-api-key", apiKey);
        })
        .build();
  }

  @Bean(name = "gemini_file_store")
  @Scope("singleton")
  public WebClient geminiFileStore() {
    return WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .defaultHeaders(headers -> {
          headers.add("Content-Type", "application/json");
          headers.add("X-goog-api-key", apiKey);
        })
        .build();
  }

  @Bean(name = "gemini_file_upload")
  @Scope("singleton")
  public WebClient geminiFileUpload() {
    return WebClient.builder()
        .baseUrl("https://generativelanguage.googleapis.com/upload/v1beta/")
        .defaultHeaders(headers -> {
          headers.add("X-goog-api-key", apiKey);
        })
        .build();
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}

