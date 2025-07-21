package com.virtualartgallery.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virtualartgallery.entity.User;

@Repository
public interface AdminDashboardRepository extends JpaRepository<User, Long> {

    long countByLastLoginAfter(LocalDateTime lastLogin);    
}
