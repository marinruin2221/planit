package cteam.planit.main.controller;

import cteam.planit.main.entity.Rating;
import cteam.planit.main.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RatingController {

  private final RatingService ratingService;

  @GetMapping("/{contentId}")
  public ResponseEntity<Rating> getRating(@PathVariable String contentId) {
    // 평점이 없으면 더미 데이터로 초기화
    Rating rating = ratingService.getRatingByContentId(contentId)
        .orElseGet(() -> ratingService.initializeRatingWithDummy(contentId));

    return ResponseEntity.ok(rating);
  }
}
