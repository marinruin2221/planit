package cteam.planit.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<UsersDAO, Long>, JpaSpecificationExecutor<UsersDAO>
{
	Optional<UsersDAO> findById(Long id);
	Optional<UsersDAO> findByUserIdAndUserPw(String userId, String userPw);
}