package cteam.planit.main.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.UserListItemDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevUserController {

    private final UsersRepository usersRepository;

    // ✅ 개발용: 가입된 유저 목록 확인
    @GetMapping("/users")
    public List<UserListItemDTO> listUsers() {
        return usersRepository.findAll().stream()
            .map(u -> new UserListItemDTO(
                u.getId(),
                u.getUserId(),
                u.getName(),
                u.getUserPw(),
                u.getEmail(),
                u.getBirthY(),
                u.getBirthM(),
                u.getBirthD(),
                u.getGender(),
                u.getDeleteYN()
            ))
            .toList();
    }
}
