package com.virtualartgallery.repository;

import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByUserId(Long userId);
    Optional<Artist> findByEmail(String email);
    Optional<Artist> findByUser(User user);
    Optional<Artist> findByUser_Username(String username);



}
