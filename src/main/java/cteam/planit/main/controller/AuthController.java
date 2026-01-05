package cteam.planit.main.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.LoginRequestDTO;
import cteam.planit.main.dto.LoginResponseDTO;
import cteam.planit.main.dto.SignupRequestDTO;
import cteam.planit.main.dto.SignupResponseDTO;
import cteam.planit.main.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;

    // 프론트: 가입하기 버튼에서 POST 요청 보낼 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO req) {
        SignupResponseDTO res = authService.signup(req);
        return ResponseEntity.status(201).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req,
                                                  HttpServletRequest request) {
        LoginResponseDTO res = authService.login(req, request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Map.of("loggedIn", false);
        }

        String userId = authentication.getName(); // 세션 인증된 userId
        var u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 구조: 회원가입 nickname을 UsersDAO.name에 저장 중이므로 name을 nickname으로 내려줍니다.
        return Map.of(
                "loggedIn", true,
                "userId", u.getUserId(),
                "nickname", u.getName()
        );
    }

    /**
     * 로그아웃 (세션 무효화)
     * - 프론트에서 POST /api/auth/logout 호출 (credentials:"include" 필수)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }
    
}
