package cteam.planit.main.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cteam.planit.main.dto.TourDetailDTO;
import cteam.planit.main.dto.TourItemDTO;
import cteam.planit.main.dto.TourIntroDTO;
import cteam.planit.main.dto.RoomInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class TourApiService {

  private final WebClient webClient;
  private ObjectMapper objectMapper;

  // VisitKorea Open API Key (한국관광공사_국문 관광정보 서비스_GW)
  @org.springframework.beans.factory.annotation.Value("${tour.api.service.key}")
  private String SERVICE_KEY;
  // 국문 관광정보 서비스 API 엔드포인트
  private final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";

  public TourApiService() {
    // 버퍼 크기를 16MB로 증가 (기본 256KB)
    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer
            .defaultCodecs()
            .maxInMemorySize(16 * 1024 * 1024)) // 16MB
        .build();

    this.webClient = WebClient.builder()
        .baseUrl(BASE_URL)
        .exchangeStrategies(strategies)
        .build();
    this.objectMapper = new ObjectMapper();
  }

  public cteam.planit.main.dto.TourPageDTO getAreaBasedList(List<String> areaCodes, List<String> categories, int page,
      int size) {
    // 캐시된 데이터가 없으면 빈 결과 반환
    if (cachedTourList == null || cachedTourList.isEmpty()) {
      return new cteam.planit.main.dto.TourPageDTO(new ArrayList<>(), 0);
    }

    // 스트림 필터링
    List<TourItemDTO> filteredList = cachedTourList.stream()
        .filter(item -> {
          // 1. 지역 필터
          if (areaCodes != null && !areaCodes.isEmpty()) {
            if (!areaCodes.contains(item.getAreacode())) {
              return false;
            }
          }
          // 2. 카테고리(숙소유형) 필터
          if (categories != null && !categories.isEmpty()) {
            boolean match = false;
            for (String cat : categories) {
              if (isCategoryMatch(item.getCat3(), cat)) {
                match = true;
                break;
              }
            }
            if (!match)
              return false;
          }
          return true;
        })
        .collect(java.util.stream.Collectors.toList());

    // 페이지네이션 계산
    int totalCount = filteredList.size();
    int start = (page - 1) * size;
    int end = Math.min(start + size, totalCount);

    if (start >= totalCount) {
      return new cteam.planit.main.dto.TourPageDTO(new ArrayList<>(), totalCount);
    }

    List<TourItemDTO> pagedList = filteredList.subList(start, end);
    System.out.println("Returning cached page " + page + " (size " + size + ") from total " + totalCount);

    return new cteam.planit.main.dto.TourPageDTO(pagedList, totalCount);
  }

  // 프론트엔드 카테고리 -> VisitKorea cat3 코드 매핑
  private boolean isCategoryMatch(String itemCat3, String frontendCategory) {
    if (itemCat3 == null)
      return false;

    switch (frontendCategory) {
      case "호텔":
        return itemCat3.equals("B02010100"); // 호텔
      case "콘도미니엄":
        return itemCat3.equals("B02010200"); // 콘도미니엄
      case "펜션":
        return itemCat3.equals("B02010400"); // 펜션
      case "모텔":
        return itemCat3.equals("B02010500"); // 모텔
      case "게스트하우스":
        return itemCat3.equals("B02010700"); // 게스트하우스
      case "한옥":
        return itemCat3.equals("B02011000"); // 한옥
      case "캠핑장":
        return itemCat3.equals("B02011100"); // 캠핑장
      case "글램핑":
        return itemCat3.equals("B02011200"); // 글램핑
      case "기타":
      case "야영장 및 기타":
        // 기타 숙박 (B02011600) 및 야영장 (A03020200)
        return itemCat3.equals("B02011600") || itemCat3.equals("A03020200");
      default:
        return false;
    }
  }

  public TourDetailDTO getDetailCommon(String contentId) {
    TourDetailDTO detailDTO = null;
    try {
      System.out.println("=== Calling VisitKorea Detail API for contentId: " + contentId + " ===");

      String jsonResponse = webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/detailCommon2")
              .queryParam("serviceKey", SERVICE_KEY)
              .queryParam("MobileOS", "ETC")
              .queryParam("MobileApp", "Planit")
              .queryParam("_type", "json")
              .queryParam("contentId", contentId)
              .queryParam("defaultYN", "Y")
              .queryParam("firstImageYN", "Y")
              .queryParam("addrinfoYN", "Y")
              .queryParam("mapinfoYN", "Y")
              .queryParam("overviewYN", "Y")
              .build())
          .retrieve()
          .bodyToMono(String.class)
          .block();

      System.out.println("Detail API Response: "
          + (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray() && items.size() > 0) {
          detailDTO = objectMapper.treeToValue(items.get(0), TourDetailDTO.class);
        }
      }

    } catch (Exception e) {
      System.err.println("Error calling detailCommon2 API: " + e.getMessage());
      e.printStackTrace();
    }
    return detailDTO;
  }

  /**
   * contentId로 특정 숙박 항목 조회
   * KorService2에는 detailCommon 엔드포인트가 없으므로
   * searchStay2 API로 검색 후 contentId로 필터링
   */
  public TourItemDTO getItemByContentId(String contentId) {
    try {
      System.out.println("=== Searching for contentId: " + contentId + " ===");

      // searchStay2에서 많은 데이터를 가져와서 contentId로 필터링
      // 더 효율적인 방법이 있다면 대체 가능
      String jsonResponse = webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/searchStay2")
              .queryParam("serviceKey", SERVICE_KEY)
              .queryParam("numOfRows", 1000) // 충분히 많이 가져옴
              .queryParam("pageNo", 1)
              .queryParam("MobileOS", "ETC")
              .queryParam("MobileApp", "Planit")
              .queryParam("_type", "json")
              .queryParam("arrange", "A")
              .build())
          .retrieve()
          .bodyToMono(String.class)
          .block();

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray()) {
          for (JsonNode item : items) {
            String itemContentId = item.path("contentid").asText();
            if (contentId.equals(itemContentId)) {
              TourItemDTO dto = objectMapper.treeToValue(item, TourItemDTO.class);
              System.out.println("Found item: " + dto.getTitle());
              return dto;
            }
          }
        }
      }
      System.out.println("Item not found for contentId: " + contentId);
    } catch (Exception e) {
      System.err.println("Error searching for contentId: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * contentId로 상세 이미지 목록 조회
   * KorService2/detailImage2 API 사용
   */
  public List<String> getDetailImages(String contentId) {
    List<String> imageUrls = new ArrayList<>();
    try {
      System.out.println("=== Fetching detail images for contentId: " + contentId + " ===");

      String jsonResponse = webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/detailImage2")
              .queryParam("serviceKey", SERVICE_KEY)
              .queryParam("MobileOS", "ETC")
              .queryParam("MobileApp", "Planit")
              .queryParam("_type", "json")
              .queryParam("contentId", contentId)
              .queryParam("imageYN", "Y")
              .build())
          .retrieve()
          .bodyToMono(String.class)
          .block();

      System.out.println("Image API Response: "
          + (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray()) {
          for (JsonNode item : items) {
            String originimgurl = item.path("originimgurl").asText();
            if (originimgurl != null && !originimgurl.isEmpty()) {
              imageUrls.add(originimgurl);
            }
          }
          System.out.println("Found " + imageUrls.size() + " images");
        } else if (!items.isMissingNode()) {
          // 단일 이미지인 경우
          String originimgurl = items.path("originimgurl").asText();
          if (originimgurl != null && !originimgurl.isEmpty()) {
            imageUrls.add(originimgurl);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error fetching detail images: " + e.getMessage());
      e.printStackTrace();
    }
    return imageUrls;
  }

  /**
   * contentId로 소개 정보 조회 (체크인, 체크아웃, 부대시설 등)
   * KorService2/detailIntro1 API 사용
   */
  public TourIntroDTO getDetailIntro(String contentId, String contentTypeId) {
    try {
      System.out.println("=== Fetching detail intro for contentId: " + contentId + " ===");

      DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
      java.net.URI uri = factory.builder()
          .path("/detailIntro1")
          .queryParam("serviceKey", SERVICE_KEY)
          .queryParam("MobileOS", "ETC")
          .queryParam("MobileApp", "Planit")
          .queryParam("_type", "json")
          .queryParam("contentId", contentId)
          .queryParam("contentTypeId", contentTypeId)
          .build();

      System.out.println("Request URI: " + uri);

      String jsonResponse = webClient.get()
          .uri(uri)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      System.out.println("Intro API Response: "
          + (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray() && items.size() > 0) {
          return objectMapper.treeToValue(items.get(0), TourIntroDTO.class);
        } else if (!items.isMissingNode() && !items.isArray()) {
          // 단일 객체인 경우
          return objectMapper.treeToValue(items, TourIntroDTO.class);
        }
      }
    } catch (Exception e) {
      System.err.println("Error fetching detail intro: " + e.getMessage());
      e.printStackTrace();
    }
    // Return empty DTO to avoid 404
    return new TourIntroDTO();
  }

  /**
   * contentId로 객실 상세 정보 조회
   * KorService2/detailInfo1 API 사용
   * 숙박(contentTypeId=32)의 경우 객실별 정보 반환
   */
  public List<RoomInfoDTO> getDetailInfo(String contentId, String contentTypeId) {
    List<RoomInfoDTO> roomList = new ArrayList<>();
    try {
      System.out.println("=== Fetching detail info (rooms) for contentId: " + contentId + " ===");

      DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
      java.net.URI uri = factory.builder()
          .path("/detailInfo1")
          .queryParam("serviceKey", SERVICE_KEY)
          .queryParam("MobileOS", "ETC")
          .queryParam("MobileApp", "Planit")
          .queryParam("_type", "json")
          .queryParam("contentId", contentId)
          .queryParam("contentTypeId", contentTypeId)
          .build();

      System.out.println("Request URI: " + uri);

      String jsonResponse = webClient.get()
          .uri(uri)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      System.out.println("DetailInfo API Response: "
          + (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray()) {
          for (JsonNode item : items) {
            RoomInfoDTO room = objectMapper.treeToValue(item, RoomInfoDTO.class);
            roomList.add(room);
          }
          System.out.println("Found " + roomList.size() + " rooms");
        } else if (!items.isMissingNode()) {
          // 단일 객체인 경우
          RoomInfoDTO room = objectMapper.treeToValue(items, RoomInfoDTO.class);
          roomList.add(room);
        }
      }
    } catch (Exception e) {
      System.err.println("Error fetching detail info: " + e.getMessage());
      e.printStackTrace();
    }
    return roomList;
  }

  // 캐시된 전체 데이터 리스트
  private List<TourItemDTO> cachedTourList = new ArrayList<>();

  // 서버 시작 시 데이터 로드 (비동기 처리 권장되지만, 간단한 구현을 위해 동기 처리)
  @jakarta.annotation.PostConstruct
  public void init() {
    new Thread(this::loadAllData).start();
  }

  /**
   * 전체 데이터 로드 및 캐싱
   * numOfRows를 크게 설정하여 한 번에 가져오거나 반복해서 가져옴
   */
  public void loadAllData() {
    System.out.println("=== Starting to load all tour data for caching ===");
    List<TourItemDTO> allItems = new ArrayList<>();
    int page = 1;
    int size = 1000; // 한 번에 가져올 크기
    boolean hasMore = true;

    while (hasMore) {
      try {
        System.out.println("Fetching page " + page + "...");
        final int currentPage = page;
        // areaBasedList2 API 사용 - cat3 필드가 포함됨 (searchStay2는 cat3 미포함)
        String jsonResponse = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/areaBasedList2")
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("numOfRows", size)
                .queryParam("pageNo", currentPage)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "Planit")
                .queryParam("_type", "json")
                .queryParam("arrange", "A")
                .queryParam("contentTypeId", "32") // 32 = 숙박
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();

        if (jsonResponse != null) {
          JsonNode root = objectMapper.readTree(jsonResponse);
          JsonNode items = root.path("response").path("body").path("items").path("item");

          if (items.isArray() && items.size() > 0) {
            for (JsonNode item : items) {
              TourItemDTO dto = objectMapper.treeToValue(item, TourItemDTO.class);
              allItems.add(dto);
            }
            // 가져온 개수가 요청한 사이즈보다 작으면 마지막 페이지
            if (items.size() < size) {
              hasMore = false;
            } else {
              page++;
            }
          } else {
            hasMore = false;
          }
        } else {
          hasMore = false;
        }
      } catch (Exception e) {
        System.err.println("Error loading data page " + page + ": " + e.getMessage());
        hasMore = false; // 에러 발생 시 중단
      }
    }

    this.cachedTourList = allItems;
    System.out.println("=== Finished loading all tour data. Total cached: " + cachedTourList.size() + " ===");
  }

  /**
   * 캐시된 데이터 기반으로 이미지 유무 통계 반환
   */
  public java.util.Map<String, Integer> getImageStats() {
    int total = cachedTourList.size();
    int withImage = 0;
    int withoutImage = 0;

    for (TourItemDTO item : cachedTourList) {
      boolean hasImage = (item.getFirstimage() != null && !item.getFirstimage().isEmpty()) ||
          (item.getFirstimage2() != null && !item.getFirstimage2().isEmpty());
      if (hasImage) {
        withImage++;
      } else {
        withoutImage++;
      }
    }

    java.util.Map<String, Integer> stats = new java.util.HashMap<>();
    stats.put("total", total);
    stats.put("withImage", withImage);
    stats.put("withoutImage", withoutImage);

    return stats;
  }
}
