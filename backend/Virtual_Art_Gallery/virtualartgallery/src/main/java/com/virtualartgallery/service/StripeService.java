package com.virtualartgallery.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.PaymentRepository;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public StripeService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntent createPaymentIntent(Long orderId) throws StripeException {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

    // Check if a payment already exists for this order
    Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
    if (existingPayment.isPresent()) {
        throw new RuntimeException("Payment already exists for this order with Transaction ID: " 
                                   + existingPayment.get().getTransactionId());
    }

    long amountInPaise = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();

    Map<String, Object> params = new HashMap<>();
    params.put("amount", amountInPaise);
    params.put("currency", "inr");
    params.put("payment_method_types", Arrays.asList("card"));
    params.put("description", "Payment for Artwork Order ID: " + orderId);

    // ✅ Create metadata map separately
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("orderId", orderId); // You can add more like userId, email, etc. if needed
    params.put("metadata", metadata); // ✅ Add metadata here

    PaymentIntent paymentIntent = PaymentIntent.create(params);

    Payment payment = new Payment();
    payment.setOrder(order);
    payment.setTransactionId(paymentIntent.getId());
    payment.setAmount(order.getTotalPrice());
    payment.setStatus(PaymentStatus.PENDING);
    payment.setPaymentMethod("Stripe");
    payment.setArtwork(order.getArtwork());

    try {
        paymentRepository.save(payment);
    } catch (DataIntegrityViolationException e) {
        throw new RuntimeException("Duplicate transaction ID: " + paymentIntent.getId(), e);
    }

    return paymentIntent;
}

    public void updatePaymentStatus(Long paymentId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    public PaymentIntent retrievePaymentIntent(String transactionId) throws StripeException {
        return PaymentIntent.retrieve(transactionId);
    }
}
