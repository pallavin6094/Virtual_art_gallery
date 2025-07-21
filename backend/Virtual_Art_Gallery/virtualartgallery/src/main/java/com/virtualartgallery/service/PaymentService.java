package com.virtualartgallery.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.virtualartgallery.dto.PaymentConfirmationDto;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.entity.User;
import com.virtualartgallery.exception.ResourceNotFoundException;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.PaymentRepository;
import com.virtualartgallery.repository.UserRepository;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

public Payment getPaymentByOrder(Long orderId, String username) {
    // ✅ Fetch payment details
    Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment record not found for Order ID: " + orderId));

    // ✅ Fetch user details
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // ✅ Check if the user is authorized (Buyer or Admin)
    if (!payment.getOrder().getBuyer().getUser().getUsername().equals(username) &&
        !user.getRole().name().equals("ADMIN")) {
        throw new AccessDeniedException("You are not authorized to view this payment.");
    }

    return payment;
}

public String confirmPayment(PaymentConfirmationDto dto) {
    Payment payment = paymentRepository.findByTransactionId(dto.getTransactionId());
    if (payment == null) {
        throw new ResourceNotFoundException("Payment not found");
    }
    try {
        // ✅ Retrieve actual status from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(dto.getTransactionId());
        String stripeStatus = paymentIntent.getStatus();

        if (!"succeeded".equalsIgnoreCase(stripeStatus)) {
            return "❌ Payment is not completed in Stripe. Current Status: " + stripeStatus;
        }

        // ✅ Set payment status and purchase date
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPurchaseDate(LocalDateTime.now());

        // ✅ Update order status
        Order order = payment.getOrder();
        
        order.setOrderStatus(OrderStatus.COMPLETED);

        // ✅ Set artwork inside payment (new change)
         Artwork artwork = order.getArtwork();
         payment.setArtwork(artwork);

        // ✅ Save both
        paymentRepository.save(payment);
        orderRepository.save(order);

        return "✅ Payment and Order updated successfully at " + payment.getPurchaseDate();

    } catch (StripeException e) {
        e.printStackTrace();
        return "❌ Error verifying payment with Stripe: " + e.getMessage();
    }
}

}
