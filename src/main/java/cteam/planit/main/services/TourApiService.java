package cteam.planit.main.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cteam.planit.main.dto.TourDetailDTO;
import cteam.planit.main.dto.TourItemDTO;
import cteam.planit.main.dto.TourIntroDTO;
import cteam.planit.main.dto.RoomInfoDTO;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.repository.AccommodationRepository;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
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
  private final TransactionTemplate transactionTemplate;

  public TourApiService(GoogleImageSearchService googleImageSearchService,
      AccommodationRepository accommodationRepository,
      PlatformTransactionManager transactionManager) {
    this.googleImageSearchService = googleImageSearchService;
    this.accommodationRepository = accommodationRepository;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.transactionTemplate.setTimeout(60); // 트랜잭션 타임아웃 60초 설정

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
      int size, Integer minPrice, Integer maxPrice) {

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

    // Repository 조회 (동적 쿼리 사용)
    // 리스트가 비어있으면 null로 전달하여 쿼리에서 무시하도록 함
    List<String> effectiveAreaCodes = (areaCodes != null && !areaCodes.isEmpty()) ? areaCodes : null;
    List<String> effectiveCat3List = (cat3List != null && !cat3List.isEmpty()) ? cat3List : null;

    accommodationPage = accommodationRepository.findWithFilters(effectiveAreaCodes, effectiveCat3List, minPrice,
        maxPrice, pageable);

    // Entity -> DTO 변환 및 이미지 보강
    List<TourItemDTO> dtoList = accommodationPage.getContent().stream()
        .map(entity -> {
          TourItemDTO dto = convertToDTO(entity);

          // 이미지가 없는 경우 Google 검색 시도 (리스트 조회 시에도 적용)
          String firstImage = dto.getFirstimage();
          log.info("Checking image for {}: '{}'", dto.getTitle(), firstImage); // 디버깅용 로그

          if (firstImage == null || firstImage.trim().isEmpty() || firstImage.contains(
              "https://us1.discourse-cdn.com/fedoraproject/original/3X/3/b/3b6fe33d77c5c8c8d930a933d08dc68567f5d131.jpeg")) {
            try {
              log.info("Image missing for list item {}, searching Google... (DISABLED)", dto.getTitle());
              /*
               * Google API Disabled
               * String searchImage = googleImageSearchService.searchImage(dto.getTitle() +
               * " 숙소 건물");
               * 
               * if (searchImage != null) {
               * log.info("Found image from Google: {}", searchImage);
               * dto.setFirstimage(searchImage);
               * 
               * // DB 업데이트 (비동기로 처리하여 응답 속도 저하 최소화 권장되지만, 여기선 간단히 동기 처리)
               * entity.setFirstimage(searchImage);
               * accommodationRepository.save(entity);
               * }
               */
            } catch (Exception e) {
              log.error("Error searching image for {}: {}", dto.getTitle(), e.getMessage());
            }
          }
          return dto;
        })
        .collect(Collectors.toList());

    log.info("Returning DB page {} (size {}) from total {}", page, size, accommodationPage.getTotalElements());

    return new cteam.planit.main.dto.TourPageDTO(dtoList, (int) accommodationPage.getTotalElements());
  }

  // contentId를 시드로 사용하여 항상 일관된 예상 가격 생성 (공유 로직)
  public int generateEstimatedPrice(String contentId) {
    long seed = contentId.hashCode();
    java.util.Random random = new java.util.Random(seed);

    // 기본 범위: 5만원 ~ 25만원
    int minBase = 50000;
    int maxBase = 250000;

    // 1000원 단위로 끊기
    int price = minBase + random.nextInt(maxBase - minBase);
    price = (price / 1000) * 1000;

    return price;
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
    dto.setMinPrice(entity.getMinPrice());
    return dto;
  }

  public TourDetailDTO getDetailCommon(String contentId) {
    TourDetailDTO detailDTO = null;
    try {
      log.info("=== Calling VisitKorea Detail API for contentId: {} ===", contentId);

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

      log.info("Detail API Response: {}",
          (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray() && items.size() > 0) {
          detailDTO = objectMapper.treeToValue(items.get(0), TourDetailDTO.class);
        }
      }

      // 이미지가 없는 경우 Google 검색 시도
      if (detailDTO != null && (detailDTO.getFirstimage() == null || detailDTO.getFirstimage().isEmpty())) {
        log.info("Image missing for {}, searching Google... (DISABLED)", detailDTO.getTitle());
        /*
         * Google API Disabled
         * String searchImage =
         * googleImageSearchService.searchImage(detailDTO.getTitle() + " 외관");
         * if (searchImage != null) {
         * log.info("Found image from Google: {}", searchImage);
         * detailDTO.setFirstimage(searchImage);
         * } else {
         * log.info("No image found from Google.");
         * }
         */
      }

    } catch (Exception e) {
      log.error("Error calling detailCommon2 API: {}", e.getMessage());
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
      log.info("=== Searching for contentId: {} ===", contentId);

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
                log.info("Image missing for {}, searching Google... (DISABLED)", dto.getTitle());
                /*
                 * Google API Disabled
                 * String searchImage = googleImageSearchService.searchImage(dto.getTitle() +
                 * " 외관");
                 * if (searchImage != null) {
                 * log.info("Found image from Google: {}", searchImage);
                 * dto.setFirstimage(searchImage);
                 * }
                 */
              }

              log.info("Found item: {}", dto.getTitle());
              return dto;
            }
          }
        }
      }
      log.info("Item not found for contentId: {}", contentId);
    } catch (Exception e) {
      log.error("Error searching for contentId: {}", e.getMessage());
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
      log.info("=== Fetching detail images for contentId: {} ===", contentId);

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

      log.info("Image API Response: {}",
          (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

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
          log.info("Found {} images", imageUrls.size());
        } else if (!items.isMissingNode()) {
          // 단일 이미지인 경우
          String originimgurl = items.path("originimgurl").asText();
          if (originimgurl != null && !originimgurl.isEmpty()) {
            imageUrls.add(originimgurl);
          }
        }
      }
    } catch (Exception e) {
      log.error("Error fetching detail images: {}", e.getMessage());
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
      log.info("=== Fetching detail intro for contentId: {} ===", contentId);

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

      log.info("Request URI: {}", uri);

      String jsonResponse = webClient.get()
          .uri(uri)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      log.info("Intro API Response: {}",
          (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

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
      log.error("Error fetching detail intro: {}", e.getMessage());
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
      log.info("=== Fetching detail info (rooms) for contentId: {} ===", contentId);

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

      log.info("Request URI: {}", uri);

      String jsonResponse = webClient.get()
          .uri(uri)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      log.info("DetailInfo API Response: {}",
          (jsonResponse != null ? jsonResponse.substring(0, Math.min(500, jsonResponse.length())) : "null"));

      if (jsonResponse != null) {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isArray()) {
          for (JsonNode item : items) {
            RoomInfoDTO room = objectMapper.treeToValue(item, RoomInfoDTO.class);
            roomList.add(room);
          }
          log.info("Found {} rooms", roomList.size());
        } else if (!items.isMissingNode()) {
          // 단일 객체인 경우
          RoomInfoDTO room = objectMapper.treeToValue(items, RoomInfoDTO.class);
          roomList.add(room);
        }
      }
    } catch (Exception e) {
      log.error("Error fetching detail info: {}", e.getMessage());
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
    log.info("=== Starting to load all tour data for caching ===");
    List<TourItemDTO> allItems = new ArrayList<>();
    int page = 1;
    int size = 1000; // 한 번에 가져올 크기
    boolean hasMore = true;

    while (hasMore) {
      try {
        // 컨텍스트가 닫혔는지 확인하는 간단한 방법은 없지만, 예외 발생 시 루프 탈출
        log.info("Fetching page {}...", page);
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
        // 컨텍스트 종료 등으로 인한 에러일 수 있으므로 로그 남기고 중단
        log.error("Error loading data page {} (Stopping background task): {}", page, e.getMessage());
        hasMore = false;
        return; // 스레드 종료
      }
    }

    this.cachedTourList = allItems;
    log.info("=== Finished loading all tour data. Total cached: {} ===", cachedTourList.size());

    try {
      // DB에 저장
      saveToDatabase(allItems);

      // 가격 데이터 보정 (minPrice가 NULL인 숙소에 예상 가격 설정)
      ensureMinPrices();
    } catch (Exception e) {
      log.error("Error during DB save/update in background task: {}", e.getMessage());
    }
  }

  /**
   * API에서 가져온 데이터를 DB에 저장
   */
  private void saveToDatabase(List<TourItemDTO> items) {
    log.info("=== Starting to save data to database ===");

    // 데이터가 이미 충분히 존재하면 저장 스킵 (기존 3600개 이상)
    // 200개 등 일부만 저장된 경우 재시도를 위해 기준을 3000개로 설정
    long currentCount = accommodationRepository.count();
    if (currentCount > 3000) {
      log.info("Data already exists ({} items). Skipping save operation.", currentCount);
      return;
    }

    int savedCount = 0;
    int skippedCount = 0;

    for (TourItemDTO dto : items) {
      try {
        // 이미 존재하는 데이터는 업데이트, 없으면 삽입
        // DTO에 가격 정보가 없더라도 convertToEntity 내부가 아니라 여기서 챙기기는 어려움
        // ensureMinPrices가 처리할 것임
        Accommodation entity = convertToEntity(dto);
        accommodationRepository.save(entity);
        savedCount++;
      } catch (Exception e) {
        log.error("Error saving contentId {}: {}", dto.getContentid(), e.getMessage());
        skippedCount++;
      }
    }

    log.info("=== Finished saving to database. Saved: {}, Skipped: {} ===", savedCount, skippedCount);
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
        .modifiedtime(dto.getModifiedtime())
        .minPrice(dto.getMinPrice() != null ? dto.getMinPrice() : generateEstimatedPrice(dto.getContentid())) // 가격 없을 시
                                                                                                              // 자동 생성
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

  // DB의 minPrice가 비어있는 경우 채워주는 보정 로직
  private void ensureMinPrices() {
    log.info("=== Checking for accommodations with missing minPrice ===");
    List<Accommodation> missingPriceList = accommodationRepository.findByMinPriceIsNull();

    if (missingPriceList.isEmpty()) {
      log.info("=== All accommodations have minPrice. No update needed. ===");
      return;
    }

    log.info("=== Found {} accommodations without minPrice. Updating... ===", missingPriceList.size());

    // 배치 처리 (트랜잭션 분할 및 DB 부하 감소)
    int batchSize = 100;
    int totalUpdated = 0;

    for (int i = 0; i < missingPriceList.size(); i += batchSize) {
      int end = Math.min(i + batchSize, missingPriceList.size());
      List<Accommodation> batch = missingPriceList.subList(i, end);

      try {
        // 트랜잭션 내에서 실행하여 세션 유지 및 롤백 지원
        transactionTemplate.execute(status -> {
          for (Accommodation acc : batch) {
            int estimated = generateEstimatedPrice(acc.getContentId());
            acc.setMinPrice(estimated);
          }
          accommodationRepository.saveAll(batch);
          return null;
        });
        totalUpdated += batch.size();
        log.info("Updated batch {} ({}/{})", (i / batchSize) + 1, totalUpdated, missingPriceList.size());
      } catch (Exception e) {
        log.error("Error updating batch {}: {}", (i / batchSize) + 1, e.getMessage());
      }
    }

    log.info("=== Finished updating minPrice. Total updated: {} ===", totalUpdated);
  }
}
