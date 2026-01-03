package cteam.planit.main.repository;

import cteam.planit.main.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByContentIdOrderByReviewDateDesc(String contentId);

  int countByContentId(String contentId);

  @Query("SELECT AVG(r.stars) FROM Review r WHERE r.contentId = :contentId")
  Double getAverageStarsByContentId(String contentId);

  Page<Review> findByUsersIdAndDeleteYNAndNameContaining(Long usersId, String deleteYN, String word, Pageable pageable);
}
