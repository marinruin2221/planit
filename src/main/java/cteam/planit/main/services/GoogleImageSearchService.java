package cteam.planit.main.services;

import cteam.planit.main.dto.GoogleSearchResponse;
import cteam.planit.main.dto.GoogleSearchItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleImageSearchService {

  private static final Logger logger = LoggerFactory.getLogger(GoogleImageSearchService.class);
  private static final String GOOGLE_SEARCH_URL = "https://www.googleapis.com/customsearch/v1";

  @Value("${google.api.key:}")
  private String apiKey;

  @Value("${google.search.cx:}")
  private String searchCx;

  private final RestTemplate restTemplate;

  public GoogleImageSearchService() {
    this.restTemplate = new RestTemplate();
  }

  public String searchImage(String query) {
    if (apiKey == null || apiKey.isEmpty() || searchCx == null || searchCx.isEmpty()) {
      logger.warn("Google API Key or CX is not configured. Skipping image search.");
      return null;
    }

    try {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_SEARCH_URL)
          .queryParam("key", apiKey)
          .queryParam("cx", searchCx)
          .queryParam("q", query)
          .queryParam("searchType", "image")
          .queryParam("num", 1); // 1개의 결과만 요청

      GoogleSearchResponse response = restTemplate.getForObject(builder.toUriString(), GoogleSearchResponse.class);

      if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
        GoogleSearchItem item = response.getItems().get(0);
        return item.getLink(); // 원본 이미지 링크 반환
      }
    } catch (Exception e) {
      logger.error("Error searching image for query: " + query, e);
    }

    return null;
  }
}
