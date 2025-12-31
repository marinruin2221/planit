package cteam.planit.main.controller;

import cteam.planit.main.entity.Review;
import cteam.planit.main.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

  private final ReviewService reviewService;

  @GetMapping("/{contentId}")
  public ResponseEntity<List<Review>> getReviews(@PathVariable String contentId) {
    List<Review> reviews = reviewService.getReviewsByContentId(contentId);
    return ResponseEntity.ok(reviews);
  }

  @PostMapping
  public ResponseEntity<Review> addReview(@RequestBody Review review) {
    Review saved = reviewService.addReview(review);
    return ResponseEntity.ok(saved);
  }
}
