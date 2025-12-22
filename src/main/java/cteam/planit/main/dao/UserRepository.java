package com.example.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByUsernameIgnoreCase(String username);
}
