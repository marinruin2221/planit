package cteam.planit.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cteam.planit.main.dto.SignupRequestDTO;
import cteam.planit.main.dto.SignupResponseDTO;
import cteam.planit.main.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 프론트: 가입하기 버튼에서 POST 요청 보낼 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO req) {
        SignupResponseDTO res = authService.signup(req);
        return ResponseEntity.status(201).body(res);
    }
}
