package cteam.planit.main.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BreakdownRepository extends JpaRepository<BreakdownDAO, Long>, JpaSpecificationExecutor<BreakdownDAO>
{
	Page<BreakdownDAO> findByUserIdAndDeleteYNAndNameContaining(String userId, String deleteYN, String word, Pageable pageable);

	@Modifying
	@Query
	("""
		UPDATE BreakdownDAO b
		SET b.status = '3'
		WHERE b.userId = :userId
		AND b.deleteYN = 'N'
		AND b.status = '1'
		AND b.dateT <= :today
	""")
	void updateCompletedByCheckoutDate(@Param("userId") String userId, @Param("today") String today);
}