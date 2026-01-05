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

    List<Accommodation> findTop10ByOrderByMinPriceAsc();

    @org.springframework.data.jpa.repository.Query(
        "SELECT a FROM Accommodation a WHERE " +
        "(:areaCodes IS NULL OR a.areacode IN :areaCodes) AND " +
        "(:categories IS NULL OR a.cat3 IN :categories) AND " +
        "(:minPrice IS NULL OR a.minPrice IS NULL OR a.minPrice >= :minPrice) AND " +
        "(:maxPrice IS NULL OR a.minPrice IS NULL OR a.minPrice <= :maxPrice) AND " +
        "(" +
        "  :keyword IS NULL OR " +
        "  a.title LIKE %:keyword% OR " +
        "  a.addr1 LIKE %:keyword% OR " +
        "  a.addr2 LIKE %:keyword%" +
        ")"
    )
    Page<Accommodation> findWithFilters(
            @org.springframework.data.repository.query.Param("areaCodes") List<String> areaCodes,
            @org.springframework.data.repository.query.Param("categories") List<String> categories,
            @org.springframework.data.repository.query.Param("minPrice") Integer minPrice,
            @org.springframework.data.repository.query.Param("maxPrice") Integer maxPrice,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            Pageable pageable);

    /**
     * 좌표가 있는 숙소 중 지정된 범위 내 숙소 검색 (DB 기반 위치 검색)
     * Oracle DB에서 좌표 문자열을 숫자로 변환하여 범위 비교
     */
    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM ACCOMMODATION a WHERE " +
            "a.mapx IS NOT NULL AND a.mapy IS NOT NULL AND " +
            "TO_NUMBER(a.mapx) BETWEEN :minX AND :maxX AND " +
            "TO_NUMBER(a.mapy) BETWEEN :minY AND :maxY " +
            "ORDER BY ABS(TO_NUMBER(a.mapx) - :centerX) + ABS(TO_NUMBER(a.mapy) - :centerY) " +
            "FETCH FIRST 100 ROWS ONLY", nativeQuery = true)
    List<Accommodation> findByLocationRange(
            @org.springframework.data.repository.query.Param("minX") double minX,
            @org.springframework.data.repository.query.Param("maxX") double maxX,
            @org.springframework.data.repository.query.Param("minY") double minY,
            @org.springframework.data.repository.query.Param("maxY") double maxY,
            @org.springframework.data.repository.query.Param("centerX") double centerX,
            @org.springframework.data.repository.query.Param("centerY") double centerY);
}
