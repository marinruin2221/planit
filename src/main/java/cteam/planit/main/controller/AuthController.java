package cteam.planit.main.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.LoginRequestDTO;
import cteam.planit.main.dto.LoginResponseDTO;
import cteam.planit.main.dto.SigninDTO;
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

        // 로그인 안 된 경우
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Map.of("loggedIn", false);
        }

        // authentication.getName()이 항상 DB userId와 일치한다고 가정하면 위험해서
        // principal 타입별로 안전하게 처리
        String userId;

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            userId = ud.getUsername();
        } else {
            userId = authentication.getName();
        }

        // 사용자 조회 실패해도 예외 던지지 말고 loggedIn:false 처리 (프론트 안정)
        var opt = usersRepository.findByUserId(userId);
        if (opt.isEmpty()) {
            return Map.of("loggedIn", false);
        }

        var u = opt.get();

        return Map.of(
                "loggedIn", true,
                "userId", u.getUserId(),
                "nickname", u.getName(),
                "email", u.getEmail()
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
    
    @PostMapping("/findid")
    public SigninDTO findid(@RequestBody SigninDTO signinDTO)
    {
        return authService.findid(signinDTO);
    }
}
