package com.virtualartgallery.repository;

import com.virtualartgallery.entity.Buyer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByUser_Username(String username);  // ✅ Corrected query
    Optional<Buyer> findByUser_Id(Long userId);
    boolean existsByUser_Username(String username);


}
