package cteam.planit.main.configurations;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

  @Value("${openapi.title}")
  String openApiTitle;
  @Value("${openapi.version}")
  String openApiVersion;
  @Value("${openapi.description}")
  String openApiDescription;
  @Value("${openapi.contact.email}")
  String openApiContactEmail;
  @Value("${openapi.contact.name}")
  String openApiContactName;
  @Value("${openapi.contact.url}")
  String openApiContactUrl;

  @Value("${openapi.group.public.name}")
  String openApiGroupPublicName;
  @Value("${openapi.group.public.match}")
  String openApiGroupPublicMatch;
  @Value("${openapi.group.auth.name}")
  String openApiGroupAuthName;
  @Value("${openapi.group.auth.match}")
  String openApiGroupAuthMatch;
  @Value("${openapi.group.admin.name}")
  String openApiGroupAdminName;
  @Value("${openapi.group.admin.match}")
  String openApiGroupAdminMatch;

  @Bean
  public OpenAPI openApi() {
    Info info = new Info()
        .title(openApiTitle)
        .version(openApiVersion)
        .description(openApiDescription)
        .contact(new Contact()
            .email(openApiContactEmail)
            .name(openApiContactName)
            .url(openApiContactUrl));

    return new OpenAPI()
        .components(new Components())
        .info(info);
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group(openApiGroupPublicName)
        .packagesToScan("com.example.demo.controllers")
        .pathsToMatch(openApiGroupPublicMatch.split(","))
        .build();
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group(openApiGroupAuthName)
        .packagesToScan("com.example.demo.controllers")
        .pathsToMatch(openApiGroupAuthMatch.split(","))
        .build();
  }

  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
        .group(openApiGroupAdminName)
        .packagesToScan("com.example.demo.controllers")
        .pathsToMatch(openApiGroupAdminMatch.split(","))
        .build();
  }
}
