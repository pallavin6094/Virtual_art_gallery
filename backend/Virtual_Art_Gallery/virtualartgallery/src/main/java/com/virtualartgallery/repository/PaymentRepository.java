package com.virtualartgallery.repository;

import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    // Find payments based on status (e.g., COMPLETED, PENDING)
    List<Payment> findByStatus(PaymentStatus status);

    // Find payment by transaction ID
    Payment findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(Long orderId);
}
