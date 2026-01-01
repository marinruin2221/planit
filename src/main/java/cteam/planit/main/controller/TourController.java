package cteam.planit.main.controller;

import cteam.planit.main.dto.TourItemDTO;
import cteam.planit.main.dto.TourIntroDTO;
import cteam.planit.main.services.TourApiService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tours")
public class TourController {

  private final TourApiService tourApiService;

  public TourController(TourApiService tourApiService) {
    this.tourApiService = tourApiService;
  }

  @GetMapping
  public ResponseEntity<cteam.planit.main.dto.TourPageDTO> getTourList(
      @RequestParam(required = false) List<String> areaCode,
      @RequestParam(required = false) List<String> category,
      @RequestParam(required = false) Integer minPrice,
      @RequestParam(required = false) Integer maxPrice,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    log.info("=== TourController.getTourList called with areaCode: {}, category: {}, price: {}~{} ===", areaCode,
        category, minPrice, maxPrice);
    return ResponseEntity.ok(tourApiService.getAreaBasedList(areaCode, category, page, size, minPrice, maxPrice));
  }

  @GetMapping("/{contentId}")
  public ResponseEntity<TourItemDTO> getTourDetail(@PathVariable String contentId) {
    log.info("=== TourController.getTourDetail called for: {} ===", contentId);

    // KorService2에는 detailCommon 엔드포인트가 없으므로
    // searchStay2 API에서 전체 데이터를 가져와 contentId로 필터링
    TourItemDTO item = tourApiService.getItemByContentId(contentId);

    if (item != null) {
      return ResponseEntity.ok(item);
    } else {
      log.info("Item not found, returning 404");
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{contentId}/images")
  public List<String> getTourImages(@PathVariable String contentId) {
    log.info("=== TourController.getTourImages called for: {} ===", contentId);
    return tourApiService.getDetailImages(contentId);
  }

  @GetMapping("/{contentId}/intro")
  public ResponseEntity<TourIntroDTO> getTourIntro(@PathVariable String contentId,
      @RequestParam(defaultValue = "32") String contentTypeId) {
    log.info("=== TourController.getTourIntro called for: {} (type: {}) ===", contentId, contentTypeId);
    TourIntroDTO intro = tourApiService.getDetailIntro(contentId, contentTypeId);
    if (intro != null) {
      return ResponseEntity.ok(intro);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{contentId}/rooms")
  public List<cteam.planit.main.dto.RoomInfoDTO> getTourRooms(@PathVariable String contentId,
      @RequestParam(defaultValue = "32") String contentTypeId) {
    log.info("=== TourController.getTourRooms called for: {} (type: {}) ===", contentId, contentTypeId);
    return tourApiService.getDetailInfo(contentId, contentTypeId);
  }

  @GetMapping("/stats")
  public java.util.Map<String, Integer> getTourStats() {
    log.info("=== TourController.getTourStats called ===");
    return tourApiService.getImageStats();
  }

  @GetMapping("/{contentId}/price")
  public ResponseEntity<java.util.Map<String, Object>> getTourPrice(@PathVariable String contentId,
      @RequestParam(defaultValue = "32") String contentTypeId) {
    log.info("=== TourController.getTourPrice called for: {} ===", contentId);
    List<cteam.planit.main.dto.RoomInfoDTO> rooms = tourApiService.getDetailInfo(contentId, contentTypeId);

    java.util.Map<String, Object> result = new java.util.HashMap<>();
    result.put("contentId", contentId);

    Integer minPrice = null;

    if (rooms != null && !rooms.isEmpty()) {
      // Find minimum price from all rooms
      for (cteam.planit.main.dto.RoomInfoDTO room : rooms) {
        String priceStr = room.getRoomoffseasonminfee1();
        if (priceStr == null || priceStr.isEmpty()) {
          priceStr = room.getRoompeakseasonminfee1();
        }
        if (priceStr != null && !priceStr.isEmpty()) {
          try {
            int price = Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
            if (minPrice == null || price < minPrice) {
              minPrice = price;
            }
          } catch (NumberFormatException e) {
            // Skip invalid price
          }
        }
      }
    }

    if (minPrice != null) {
      result.put("minPrice", minPrice);
      result.put("hasPrice", true);
    } else {
      // 가격 데이터가 없는 경우 통계 기반 예상 가격 생성 (contentId 기반으로 고정된 랜덤값)
      int estimatedPrice = tourApiService.generateEstimatedPrice(contentId);
      result.put("minPrice", estimatedPrice);
      result.put("hasPrice", true);
      result.put("isEstimated", true);
    }

    return ResponseEntity.ok(result);
  }

  @GetMapping("/location-based")
  public ResponseEntity<List<TourItemDTO>> getLocationBasedList(
      @RequestParam double mapX,
      @RequestParam double mapY,
      @RequestParam(defaultValue = "2000") int radius) {
    log.info("=== TourController.getLocationBasedList called with mapX: {}, mapY: {}, radius: {} ===", mapX, mapY,
        radius);
    List<TourItemDTO> result = tourApiService.getLocationBasedList(mapX, mapY, radius);
    if (result != null && !result.isEmpty()) {
      return ResponseEntity.ok(result);
    } else {
      return ResponseEntity.noContent().build();
    }
  }
}
