package cteam.planit.main.services;

import cteam.planit.main.dto.GoogleSearchResponse;
import lombok.extern.slf4j.Slf4j;
import cteam.planit.main.dto.GoogleSearchItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GoogleImageSearchService {

  // private static final Logger logger =
  // LoggerFactory.getLogger(GoogleImageSearchService.class);
  private static final String GOOGLE_SEARCH_URL = "https://www.googleapis.com/customsearch/v1";

  @Value("${google.custom-search.api-key:}")
  private String apiKey;

  @Value("${google.custom-search.cx:}")
  private String searchCx;

  private final RestTemplate restTemplate;

  public GoogleImageSearchService() {
    this.restTemplate = new RestTemplate();
  }

  public String searchImage(String query) {
    // logger.info("Searching image with CX: {} for query: {}", searchCx, query);
    log.info("API Key: {}", apiKey);
    log.info("CX: {}", searchCx);
    if (apiKey == null || apiKey.isEmpty() || searchCx == null || searchCx.isEmpty()) {
      // logger.warn("Google API Key or CX is not configured. Skipping image
      // search.");
      return null;
    }

    try {
      WebClient webClient = WebClient.builder()
          .baseUrl(
              GOOGLE_SEARCH_URL + "?key=" + apiKey + "&cx=" + searchCx + "&searchType=image&num=1&q=" + query)
          .build();
      GoogleSearchResponse response = webClient.get().retrieve().bodyToMono(GoogleSearchResponse.class).block();

      // GoogleSearchResponse response =
      // restTemplate.getForObject(builder.toUriString(), GoogleSearchResponse.class);
      log.info(response.toString());
      if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
        GoogleSearchItem item = response.getItems().get(0);
        log.info(response.toString());
        return item.getLink(); // 원본 이미지 링크 반환
      }
    } catch (Exception e) {
      // logger.error("Error searching image for query: " + query + ". Reason: " +
      // e.getMessage(), e);
    }

    return null;
  }
}
