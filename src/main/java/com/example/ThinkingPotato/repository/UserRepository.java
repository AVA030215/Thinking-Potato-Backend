package com.example.ThinkingPotato.repository;

import com.example.ThinkingPotato.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Optional<User> findOptionalByEmail(String email);
    boolean existsByEmail(String email);

}



