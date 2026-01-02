package cteam.planit.main.services;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.SignupRequestDTO;
import cteam.planit.main.dto.SignupResponseDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // 프론트에서 REQUIRED_TERM_IDS로 쓰는 3개 필수 약관
    private static final String TERM_AGREEMENT = "agreement";
    private static final String TERM_PRIVACY = "privacyconsent";
    private static final String TERM_AGE14 = "age14";

    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO req) {

        // 1) 필수 약관 3개 서버에서도 반드시 검증 (프론트만 믿으면 안 됩니다)
        Map<String, Boolean> terms = req.getTerms();
        if (terms == null
            || !Boolean.TRUE.equals(terms.get(TERM_AGREEMENT))
            || !Boolean.TRUE.equals(terms.get(TERM_PRIVACY))
            || !Boolean.TRUE.equals(terms.get(TERM_AGE14))) {
            throw new IllegalArgumentException("필수 약관(3개)에 동의해야 합니다.");
        }

        // 2) gender 검증 + DB 규격으로 변환
        // UsersDAO 규격: (M:남자, F:여자)
        String gender = req.getGender();
        String dbGender;
        if ("male".equals(gender)) dbGender = "M";
        else if ("female".equals(gender)) dbGender = "F";
        else throw new IllegalArgumentException("gender는 male 또는 female 이어야 합니다.");

        // 3) 중복 체크 (아이디/이메일)
        if (usersRepository.existsByUserId(req.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (usersRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 4) UsersDAO 생성 및 매핑
        UsersDAO u = new UsersDAO();

        // 프론트 username -> DB userId
        u.setUserId(req.getUsername());

        // 비밀번호는 반드시 해시해서 저장
        u.setUserPw(passwordEncoder.encode(req.getPassword()));

        // 프론트 nickname -> DB name (현재 UsersDAO에 nickname 컬럼이 없으므로 임시로 name 사용)
        // 실명/닉네임을 분리하고 싶으면 나중에 UsersDAO에 nickname 컬럼 추가가 정석입니다.
        u.setName(req.getNickname());

        u.setEmail(req.getEmail());

        // birth: year/month/day 그대로 저장(UsersDAO가 문자열 컬럼)
        u.setBirthY(req.getBirth().getYear());
        u.setBirthM(req.getBirth().getMonth());
        u.setBirthD(req.getBirth().getDay());

        u.setGender(dbGender);

        // 가입 시 기본값: 탈퇴 아님
        u.setDeleteYN("N");

        // 5) 저장
        UsersDAO saved = usersRepository.save(u);

        // 6) 응답 DTO
        return new SignupResponseDTO(saved.getId(), saved.getUserId(), saved.getName(), saved.getEmail());
    }
}
