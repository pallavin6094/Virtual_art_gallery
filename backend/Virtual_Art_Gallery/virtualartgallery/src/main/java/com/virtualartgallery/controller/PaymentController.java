package com.virtualartgallery.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.virtualartgallery.dto.PaymentConfirmationDto;
import com.virtualartgallery.dto.PaymentDetailsDto;
import com.virtualartgallery.dto.PaymentIntentDto;
import com.virtualartgallery.dto.PaymentResponseDto;

import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.ArtworkStatus;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.repository.ArtworkRepository;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.PaymentRepository;
import com.virtualartgallery.service.PaymentService;
import com.virtualartgallery.service.StripeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ArtworkRepository artworkRepository;

    public PaymentController(PaymentService paymentService,StripeService stripeService,PaymentRepository paymentRepository,OrderRepository orderRepository,ArtworkRepository artworkRepository) {
        this.paymentService = paymentService;
        this.stripeService = stripeService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.artworkRepository = artworkRepository;
    }

    // ✅ Fetch payment details for an order
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPayment(
        @PathVariable Long orderId, 
        Authentication authentication) {

      String username = authentication.getName(); // ✅ Get logged-in username
      Payment payment = paymentService.getPaymentByOrder(orderId, username);
      PaymentResponseDto dto = new PaymentResponseDto();
      dto.setTransactionId(payment.getTransactionId());
      dto.setAmount(payment.getAmount());

    // Safe check for artworkId
    if (payment.getArtwork() != null) {
        dto.setArtworkId(payment.getArtwork().getId());
    }
      
    return ResponseEntity.ok(dto);
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentIntentDto dto) {
    try {
        PaymentIntent intent = stripeService.createPaymentIntent(dto.getOrderId());

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        response.put("transactionId", intent.getId());
        return ResponseEntity.ok(response);

    } catch (StripeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", e.getMessage()));
    }
}

   @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentConfirmationDto dto) {
         Payment payment = paymentRepository.findByTransactionId(dto.getTransactionId());
        if (payment == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        }

    try {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(dto.getStatus().toUpperCase());

        // Update Payment
        payment.setStatus(paymentStatus);
        payment.setPurchaseDate(LocalDateTime.now());

        // Update Order
        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.COMPLETED);

        // ✅ Mark artwork as SOLD
        Artwork artwork = order.getArtwork();
        artwork.setStatus(ArtworkStatus.SOLD);

        // Sync both sides
        payment.setOrder(order);
        payment.setArtwork(artwork); // ✅ Set artwork also to Payment i added here 
        order.setPayment(payment);
        artworkRepository.save(artwork);

        // Save both
        orderRepository.save(order); // ✅ Save order explicitly
        paymentRepository.save(payment); // ✅ Save payment

        System.out.println("✅ Payment and Order updated successfully at " + LocalDateTime.now());

        return ResponseEntity.ok("Payment and Order updated successfully");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
    }
}

}