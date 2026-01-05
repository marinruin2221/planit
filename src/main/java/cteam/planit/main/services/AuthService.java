package cteam.planit.main.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.LoginRequestDTO;
import cteam.planit.main.dto.LoginResponseDTO;
import cteam.planit.main.dto.SignupRequestDTO;
import cteam.planit.main.dto.SignupResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO req) {
        String email = req.getEmail().trim().toLowerCase();

        if (usersRepository.existsByUserId(req.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (usersRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UsersDAO u = new UsersDAO();
        u.setUserId(req.getUsername());
        u.setUserPw(passwordEncoder.encode(req.getPassword())); // BCrypt 저장
        u.setName(req.getNickname()); // 현재 컬럼 구조상 nickname -> name
        u.setEmail(email);
        u.setBirthY(req.getBirth().getYear());
        u.setBirthM(req.getBirth().getMonth());
        u.setBirthD(req.getBirth().getDay());
        u.setGender("male".equals(req.getGender()) ? "M" : "F");
        u.setDeleteYN("N");

        UsersDAO saved = usersRepository.save(u);

        return new SignupResponseDTO(saved.getId(), saved.getUserId(), saved.getName(), saved.getEmail());
    }

    public LoginResponseDTO login(LoginRequestDTO req, HttpServletRequest request) {
        // 1) 인증 수행(여기서 CustomUserDetailsService가 호출됨)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUserId(), req.getPassword())
        );

        // 2) 인증 정보를 SecurityContext에 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // 3) 세션 생성 (JSESSIONID 쿠키 발급 트리거)
        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            context
        );

        // 4) 응답 데이터
        UsersDAO u = usersRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if ("Y".equalsIgnoreCase(u.getDeleteYN())) {
            throw new IllegalArgumentException("탈퇴한 사용자입니다.");
        }

        return new LoginResponseDTO(u.getId(), u.getUserId(), u.getName(), u.getEmail());
    }
}
