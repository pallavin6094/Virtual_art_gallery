package com.virtualartgallery.repository;

import com.virtualartgallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // ✅ Count users who registered today
    Long countByRegistrationDateAfter(LocalDateTime startOfDay);

    // ✅ Count users who logged in today
    Long countByLastLoginAfter(LocalDateTime startOfDay);

}

