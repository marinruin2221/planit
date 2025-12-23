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
      @RequestParam(required = false) String areaCode,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    System.out.println("=== TourController.getTourList called ===");
    return ResponseEntity.ok(tourApiService.getAreaBasedList(areaCode, page, size));
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
}
