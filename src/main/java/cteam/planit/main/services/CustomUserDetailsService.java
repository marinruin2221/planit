package cteam.planit.main.services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersDAO u = usersRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if ("Y".equalsIgnoreCase(u.getDeleteYN())) {
            throw new UsernameNotFoundException("탈퇴한 사용자입니다.");
        }

        // 권한은 일단 USER 고정 (나중에 roles 컬럼 생기면 확장)
        return new org.springframework.security.core.userdetails.User(
                u.getUserId(),
                u.getUserPw(), // BCrypt 해시 비밀번호
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
