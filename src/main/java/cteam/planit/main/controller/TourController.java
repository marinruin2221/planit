package cteam.planit.main.controller;

import cteam.planit.main.dto.TourItemDTO;
import cteam.planit.main.dto.TourIntroDTO;
import cteam.planit.main.services.TourApiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    System.out.println(
        "=== TourController.getTourList called with areaCode: " + areaCode + ", category: " + category +
            ", price: " + minPrice + "~" + maxPrice + " ===");
    return ResponseEntity.ok(tourApiService.getAreaBasedList(areaCode, category, page, size, minPrice, maxPrice));
  }

  @GetMapping("/{contentId}")
  public ResponseEntity<TourItemDTO> getTourDetail(@PathVariable String contentId) {
    System.out.println("=== TourController.getTourDetail called for: " + contentId + " ===");

    // KorService2에는 detailCommon 엔드포인트가 없으므로
    // searchStay2 API에서 전체 데이터를 가져와 contentId로 필터링
    TourItemDTO item = tourApiService.getItemByContentId(contentId);

    if (item != null) {
      return ResponseEntity.ok(item);
    } else {
      System.out.println("Item not found, returning 404");
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{contentId}/images")
  public List<String> getTourImages(@PathVariable String contentId) {
    System.out.println("=== TourController.getTourImages called for: " + contentId + " ===");
    return tourApiService.getDetailImages(contentId);
  }

  @GetMapping("/{contentId}/intro")
  public ResponseEntity<TourIntroDTO> getTourIntro(@PathVariable String contentId,
      @RequestParam(defaultValue = "32") String contentTypeId) {
    System.out
        .println("=== TourController.getTourIntro called for: " + contentId + " (type: " + contentTypeId + ") ===");
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
    System.out
        .println("=== TourController.getTourRooms called for: " + contentId + " (type: " + contentTypeId + ") ===");
    return tourApiService.getDetailInfo(contentId, contentTypeId);
  }

  @GetMapping("/stats")
  public java.util.Map<String, Integer> getTourStats() {
    System.out.println("=== TourController.getTourStats called ===");
    return tourApiService.getImageStats();
  }

  @GetMapping("/{contentId}/price")
  public ResponseEntity<java.util.Map<String, Object>> getTourPrice(@PathVariable String contentId,
      @RequestParam(defaultValue = "32") String contentTypeId) {
    System.out.println("=== TourController.getTourPrice called for: " + contentId + " ===");
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
}
