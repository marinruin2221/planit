package cteam.planit.main.services;

import cteam.planit.main.entity.Rating;
import cteam.planit.main.repository.RatingRepository;
import cteam.planit.main.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

  private final RatingRepository ratingRepository;
  private final ReviewRepository reviewRepository;

  public Optional<Rating> getRatingByContentId(String contentId) {
    return ratingRepository.findByContentId(contentId);
  }

  @Transactional
  public Rating createOrUpdateRating(String contentId) {
    int reviewCount = reviewRepository.countByContentId(contentId);
    Double avgStars = reviewRepository.getAverageStarsByContentId(contentId);

    if (avgStars == null) {
      avgStars = 0.0;
    }

    // 별점(1-5)을 10점 만점으로 변환
    double score = avgStars * 2;

    // 점수에 따른 라벨 및 색상 결정
    String label;
    String colorClass;

    if (score >= 8.5) {
      label = "최고에요";
      colorClass = "text-orange-600";
    } else if (score >= 7.0) {
      label = "추천해요";
      colorClass = "text-yellow-500";
    } else if (score >= 5.0) {
      label = "괜찮아요";
      colorClass = "text-blue-500";
    } else {
      label = "보통이에요";
      colorClass = "text-gray-500";
    }

    Rating rating = ratingRepository.findByContentId(contentId)
        .orElse(Rating.builder().contentId(contentId).build());

    rating.setAverageScore(Math.round(score * 10.0) / 10.0);
    rating.setReviewCount(reviewCount);
    rating.setLabel(label);
    rating.setColorClass(colorClass);

    return ratingRepository.save(rating);
  }

  @Transactional
  public Rating initializeRatingWithDummy(String contentId) {
    // 이미 존재하면 그대로 반환
    Optional<Rating> existing = ratingRepository.findByContentId(contentId);
    if (existing.isPresent()) {
      return existing.get();
    }

    // 더미 데이터 생성 (contentId 기반 일관된 랜덤)
    int hash = contentId.hashCode();
    double score = (Math.abs(hash % 61) + 30) / 10.0;
    int reviewCount = (Math.abs((hash >> 8) % 111)) + 10;

    String label;
    String colorClass;

    if (score >= 8.5) {
      label = "최고에요";
      colorClass = "text-orange-600";
    } else if (score >= 7.0) {
      label = "추천해요";
      colorClass = "text-yellow-500";
    } else if (score >= 5.0) {
      label = "괜찮아요";
      colorClass = "text-blue-500";
    } else {
      label = "보통이에요";
      colorClass = "text-gray-500";
    }

    Rating rating = Rating.builder()
        .contentId(contentId)
        .averageScore(Math.round(score * 10.0) / 10.0)
        .reviewCount(reviewCount)
        .label(label)
        .colorClass(colorClass)
        .build();

    return ratingRepository.save(rating);
  }
}
