package cteam.planit.main.services;

import cteam.planit.main.entity.Review;
import cteam.planit.main.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RatingService ratingService;

    public List<Review> getReviewsByContentId(String contentId) {
        List<Review> reviews = reviewRepository.findByContentIdOrderByReviewDateDesc(contentId);
        
        // 리뷰가 없으면 더미 데이터 초기화
        if (reviews.isEmpty()) {
            reviews = initializeDummyReviews(contentId);
        }
        
        return reviews;
    }

    @Transactional
    public Review addReview(Review review) {
        review.setReviewDate(LocalDate.now());
        Review saved = reviewRepository.save(review);
        
        // 평점 재계산
        ratingService.createOrUpdateRating(review.getContentId());
        
        return saved;
    }

    @Transactional
    public List<Review> initializeDummyReviews(String contentId) {
        // 이미 리뷰가 있으면 반환
        List<Review> existing = reviewRepository.findByContentIdOrderByReviewDateDesc(contentId);
        if (!existing.isEmpty()) {
            return existing;
        }
        
        // 더미 리뷰 데이터
        String[] reviewerNames = {"여행러버", "힐링여행", "가족여행자", "커플여행", "솔로여행러",
                "맛집탐험가", "휴식추구", "액티비티러버", "뷰맛집탐방", "청결중시"};
        
        String[] highReviews = {
                "정말 좋았습니다! 다음에 또 방문하고 싶어요.",
                "깨끗하고 위치도 좋았습니다. 추천합니다.",
                "직원분들이 친절하셔서 기분 좋게 묵었습니다.",
                "전망이 정말 좋았어요! 사진보다 실제가 더 좋았습니다.",
                "조식도 맛있고, 전체적으로 만족스러웠어요."
        };
        
        String[] midReviews = {
                "가격 대비 무난했습니다.",
                "위치는 좋았지만 시설이 조금 노후되었어요.",
                "나쁘지 않았지만 기대만큼은 아니었어요."
        };
        
        String[] lowReviews = {
                "청소 상태가 조금 아쉬웠습니다.",
                "직원 응대가 조금 불친절했어요."
        };
        
        // contentId 기반 시드로 일관된 랜덤 생성
        int seed = contentId.hashCode();
        Random rand = new Random(seed);
        
        // 평점 정보 가져오기 (또는 초기화)
        var rating = ratingService.initializeRatingWithDummy(contentId);
        int reviewCount = rating.getReviewCount();
        double avgScore = rating.getAverageScore();
        
        List<Review> reviews = new ArrayList<>();
        
        for (int i = 0; i < reviewCount; i++) {
            // 평균 점수에 맞게 별점 분포 조정
            int stars;
            String content;
            
            double randomVal = rand.nextDouble();
            if (avgScore >= 8.0) {
                // 높은 평점 숙소: 4-5점 위주
                if (randomVal < 0.7) {
                    stars = 5;
                    content = highReviews[rand.nextInt(highReviews.length)];
                } else if (randomVal < 0.9) {
                    stars = 4;
                    content = highReviews[rand.nextInt(highReviews.length)];
                } else {
                    stars = 3;
                    content = midReviews[rand.nextInt(midReviews.length)];
                }
            } else if (avgScore >= 6.0) {
                // 중간 평점: 3-4점 위주
                if (randomVal < 0.4) {
                    stars = 4;
                    content = highReviews[rand.nextInt(highReviews.length)];
                } else if (randomVal < 0.8) {
                    stars = 3;
                    content = midReviews[rand.nextInt(midReviews.length)];
                } else {
                    stars = 2;
                    content = lowReviews[rand.nextInt(lowReviews.length)];
                }
            } else {
                // 낮은 평점: 2-3점 위주
                if (randomVal < 0.3) {
                    stars = 3;
                    content = midReviews[rand.nextInt(midReviews.length)];
                } else if (randomVal < 0.7) {
                    stars = 2;
                    content = lowReviews[rand.nextInt(lowReviews.length)];
                } else {
                    stars = 1;
                    content = lowReviews[rand.nextInt(lowReviews.length)];
                }
            }
            
            Review review = Review.builder()
                    .contentId(contentId)
                    .reviewerName(reviewerNames[rand.nextInt(reviewerNames.length)])
                    .reviewerLevel(rand.nextInt(20) + 1)
                    .stars(stars)
                    .content(content)
                    .reviewDate(LocalDate.now().minusDays(rand.nextInt(365)))
                    .build();
            
            reviews.add(review);
        }
        
        return reviewRepository.saveAll(reviews);
    }
}
