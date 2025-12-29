package cteam.planit.main.repository;

import cteam.planit.main.entity.CommonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonImageRepository extends JpaRepository<CommonImage, Long> {
  Optional<CommonImage> findByCategory(String category);
}
