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

  List<Accommodation> findByMinPriceIsNull();

  @org.springframework.data.jpa.repository.Query("SELECT a FROM Accommodation a WHERE " +
      "(:areaCodes IS NULL OR a.areacode IN :areaCodes) AND " +
      "(:categories IS NULL OR a.cat3 IN :categories) AND " +
      "(:minPrice IS NULL OR a.minPrice IS NULL OR a.minPrice >= :minPrice) AND " +
      "(:maxPrice IS NULL OR a.minPrice IS NULL OR a.minPrice <= :maxPrice)")
  Page<Accommodation> findWithFilters(
      @org.springframework.data.repository.query.Param("areaCodes") List<String> areaCodes,
      @org.springframework.data.repository.query.Param("categories") List<String> categories,
      @org.springframework.data.repository.query.Param("minPrice") Integer minPrice,
      @org.springframework.data.repository.query.Param("maxPrice") Integer maxPrice,
      Pageable pageable);
}
