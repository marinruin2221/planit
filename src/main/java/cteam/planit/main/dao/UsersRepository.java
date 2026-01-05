package cteam.planit.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<UsersDAO, Long>, JpaSpecificationExecutor<UsersDAO>
{
	Optional<UsersDAO> findById(Long id);

	// ✅ [회원가입 필수] 아이디/이메일 중복 체크용
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

	//Optional<UsersDAO> findByUserIdAndUserPw(String userId, String userPw);
	Optional<UsersDAO> findByEmailAndBirthYAndBirthMAndBirthD(String email, String birthY, String birthM, String birthD);

	// ✅ (권장) 로그인/조회용으로 아이디만 찾는 메서드
    Optional<UsersDAO> findByUserId(String userId);
}