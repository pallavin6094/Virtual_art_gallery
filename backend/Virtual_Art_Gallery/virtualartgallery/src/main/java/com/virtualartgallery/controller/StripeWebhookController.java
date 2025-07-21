package com.virtualartgallery.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.PaymentRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request) {
        String payload;
        try {
            payload = new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String paymentIdStr = session.getMetadata().get("paymentId");

                if (paymentIdStr != null) {
                    Long paymentId = Long.parseLong(paymentIdStr);
                    Payment payment = paymentRepository.findById(paymentId).orElse(null);

                    if (payment != null) {
                        payment.setStatus(PaymentStatus.COMPLETED);
                        payment.setPurchaseDate(LocalDateTime.now()); 
                        paymentRepository.save(payment);

                        Order order = payment.getOrder();
                        order.setOrderStatus(OrderStatus.COMPLETED); 
                        orderRepository.save(order);

                        System.out.println("✅ Payment and Order marked as COMPLETED for ID: " + paymentId);
                    }
                }
            }
        }

        return ResponseEntity.ok("");
    }
}
