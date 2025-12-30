package cteam.planit.main.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users>
{
	Optional<Users> findById(Long id);
	Optional<Users> findByName(String name);
	Optional<Users> findByEmail(String email);
	List<Users> findAllByIsActiveTrue();
	List<Users> findAllByIsActiveFalse();
}