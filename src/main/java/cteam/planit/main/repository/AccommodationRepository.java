package cteam.planit.main.repository;

import cteam.planit.main.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, String> {

  Optional<Accommodation> findByContentId(String contentId);

  boolean existsByContentId(String contentId);

  Page<Accommodation> findByAreacodeIn(List<String> areacodes, Pageable pageable);

  Page<Accommodation> findByCat3In(List<String> cat3List, Pageable pageable);

  Page<Accommodation> findByAreacodeInAndCat3In(List<String> areacodes, List<String> cat3List, Pageable pageable);

  long countByFirstimageIsNotNull();

  long countByFirstimageIsNull();
}
