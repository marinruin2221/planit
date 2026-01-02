// package cteam.planit.main.dao;

// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import cteam.planit.main.dto.User;

// @Repository
// public interface UserRepository extends JpaRepository<User, Long> {
//   public Optional<User> findByUsernameIgnoreCase(String username);
// }

package cteam.planit.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cteam.planit.main.dto.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByDisplayname(String displayname);

    Optional<User> findByUsername(String username);
}
