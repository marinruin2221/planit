package cteam.planit.main.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cteam.planit.main.dto.TourDetailDTO;
import cteam.planit.main.dto.TourItemDTO;
import cteam.planit.main.dto.TourIntroDTO;
import cteam.planit.main.dto.RoomInfoDTO;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.repository.AccommodationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class TourApiService {

  private final WebClient webClient;
  private ObjectMapper objectMapper;

  // VisitKorea Open API Key (한국관광공사_국문 관광정보 서비스_GW)
  @org.springframework.beans.factory.annotation.Value("${tour.api.service.key}")
  private String SERVICE_KEY;
  // 국문 관광정보 서비스 API 엔드포인트
  private final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";

  private final GoogleImageSearchService googleImageSearchService;
  private final AccommodationRepository accommodationRepository;

  public TourApiService(GoogleImageSearchService googleImageSearchService,
      AccommodationRepository accommodationRepository) {
    this.googleImageSearchService = googleImageSearchService;
    this.accommodationRepository = accommodationRepository;

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

    // PageRequest 생성 (0-based page index)
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Accommodation> accommodationPage;

    // 카테고리 매핑 (프론트엔드 한글 카테고리 -> API 코드)
    List<String> cat3List = null;
    if (categories != null && !categories.isEmpty()) {
      cat3List = new ArrayList<>();
      for (String cat : categories) {
        mapCategory(cat, cat3List);
      }
    }

    // Repository 조회 분기 처리
    boolean hasArea = areaCodes != null && !areaCodes.isEmpty();
    boolean hasCat = cat3List != null && !cat3List.isEmpty();

    if (hasArea && hasCat) {
      accommodationPage = accommodationRepository.findByAreacodeInAndCat3In(areaCodes, cat3List, pageable);
    } else if (hasArea) {
      accommodationPage = accommodationRepository.findByAreacodeIn(areaCodes, pageable);
    } else if (hasCat) {
      accommodationPage = accommodationRepository.findByCat3In(cat3List, pageable);
    } else {
      accommodationPage = accommodationRepository.findAll(pageable);
    }

    // Entity -> DTO 변환
    List<TourItemDTO> dtoList = accommodationPage.getContent().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    System.out.println(
        "Returning DB page " + page + " (size " + size + ") from total " + accommodationPage.getTotalElements());

    return new cteam.planit.main.dto.TourPageDTO(dtoList, (int) accommodationPage.getTotalElements());
  }

  // 프론트엔드 카테고리 -> VisitKorea cat3 코드 매핑 및 리스트 추가
  private void mapCategory(String frontendCategory, List<String> cat3List) {
    switch (frontendCategory) {
      case "호텔":
        cat3List.add("B02010100");
        break;
      case "콘도미니엄":
        cat3List.add("B02010200");
        break;
      case "펜션":
        cat3List.add("B02010400");
        break;
      case "모텔":
        cat3List.add("B02010500");
        break;
      case "게스트하우스":
        cat3List.add("B02010700");
        break;
      case "한옥":
        cat3List.add("B02011000");
        break;
      case "캠핑장":
        cat3List.add("B02011100");
        break;
      case "글램핑":
        cat3List.add("B02011200");
        break;
      case "기타":
      case "야영장 및 기타":
        cat3List.add("B02011600");
        cat3List.add("A03020200");
        break;
      default:
        break;
    }
  }

  // Accommodation Entity -> TourItemDTO 변환
  private TourItemDTO convertToDTO(Accommodation entity) {
    TourItemDTO dto = new TourItemDTO();
    dto.setContentid(entity.getContentId());
    dto.setTitle(entity.getTitle());
    dto.setAddr1(entity.getAddr1());
    dto.setAddr2(entity.getAddr2());
    dto.setZipcode(entity.getZipcode());
    dto.setAreacode(entity.getAreacode());
    dto.setSigungucode(entity.getSigungucode());
    dto.setCat1(entity.getCat1());
    dto.setCat2(entity.getCat2());
    dto.setCat3(entity.getCat3());
    dto.setContenttypeid(entity.getContenttypeid());
    dto.setTel(entity.getTel());
    dto.setFirstimage(entity.getFirstimage());
    dto.setFirstimage2(entity.getFirstimage2());
    dto.setMapx(entity.getMapx());
    dto.setMapy(entity.getMapy());
    dto.setMlevel(entity.getMlevel());
    dto.setCreatedtime(entity.getCreatedtime());
    dto.setModifiedtime(entity.getModifiedtime());
    return dto;
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

      // 이미지가 없는 경우 Google 검색 시도
      if (detailDTO != null && (detailDTO.getFirstimage() == null || detailDTO.getFirstimage().isEmpty())) {
        System.out.println("Image missing for " + detailDTO.getTitle() + ", searching Google...");
        String searchImage = googleImageSearchService.searchImage(detailDTO.getTitle() + " 외관");
        if (searchImage != null) {
          System.out.println("Found image from Google: " + searchImage);
          detailDTO.setFirstimage(searchImage);
        } else {
          System.out.println("No image found from Google.");
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

              // 이미지가 없는 경우 Google 검색 시도
              if (dto.getFirstimage() == null || dto.getFirstimage().isEmpty()) {
                System.out.println("Image missing for " + dto.getTitle() + ", searching Google...");
                String searchImage = googleImageSearchService.searchImage(dto.getTitle() + " 외관");
                if (searchImage != null) {
                  System.out.println("Found image from Google: " + searchImage);
                  dto.setFirstimage(searchImage);
                }
              }

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
  // @PostConstruct 대신 ApplicationReadyEvent 사용
  @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
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

    // DB에 저장
    saveToDatabase(allItems);
  }

  /**
   * API에서 가져온 데이터를 DB에 저장
   */
  private void saveToDatabase(List<TourItemDTO> items) {
    System.out.println("=== Starting to save data to database ===");

    // 데이터가 이미 존재하면 저장 스킵
    if (accommodationRepository.count() > 0) {
      System.out.println("Data already exists in database. Skipping save operation.");
      return;
    }

    int savedCount = 0;
    int skippedCount = 0;

    for (TourItemDTO dto : items) {
      try {
        // 이미 존재하는 데이터는 업데이트, 없으면 삽입
        Accommodation entity = convertToEntity(dto);
        accommodationRepository.save(entity);
        savedCount++;
      } catch (Exception e) {
        System.err.println("Error saving contentId " + dto.getContentid() + ": " + e.getMessage());
        skippedCount++;
      }
    }

    System.out.println("=== Finished saving to database. Saved: " + savedCount + ", Skipped: " + skippedCount + " ===");
  }

  /**
   * TourItemDTO를 Accommodation Entity로 변환
   */
  private Accommodation convertToEntity(TourItemDTO dto) {
    return Accommodation.builder()
        .contentId(dto.getContentid())
        .title(dto.getTitle())
        .addr1(dto.getAddr1())
        .addr2(dto.getAddr2())
        .zipcode(dto.getZipcode())
        .areacode(dto.getAreacode())
        .sigungucode(dto.getSigungucode())
        .cat1(dto.getCat1())
        .cat2(dto.getCat2())
        .cat3(dto.getCat3())
        .contenttypeid(dto.getContenttypeid())
        .tel(dto.getTel())
        .firstimage(dto.getFirstimage())
        .firstimage2(dto.getFirstimage2())
        .mapx(dto.getMapx())
        .mapy(dto.getMapy())
        .mlevel(dto.getMlevel())
        .createdtime(dto.getCreatedtime())
        .modifiedtime(dto.getModifiedtime())
        .build();
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
