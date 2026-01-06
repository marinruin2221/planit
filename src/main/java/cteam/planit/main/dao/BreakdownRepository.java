package cteam.planit.main.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BreakdownRepository extends JpaRepository<BreakdownDAO, Long>, JpaSpecificationExecutor<BreakdownDAO>
{
	Page<BreakdownDAO> findByUserIdAndDeleteYNAndNameContaining(String userId, String deleteYN, String word, Pageable pageable);
}