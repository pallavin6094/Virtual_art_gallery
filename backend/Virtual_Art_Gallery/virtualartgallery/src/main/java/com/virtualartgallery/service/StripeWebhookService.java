package com.virtualartgallery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.PaymentRepository;

@Service
public class StripeWebhookService {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public StripeWebhookService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public String handleWebhook(String payload, String sigHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
    
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("Failed to parse payment intent"));
    
            String transactionId = paymentIntent.getId();

            // ✅ Extract orderId from metadata
            String orderId = paymentIntent.getMetadata().get("orderId");
            System.out.println("Received orderId from Stripe metadata: " + orderId);
    
            // Find the payment by Stripe transaction ID
            Payment payment = paymentRepository.findByTransactionId(transactionId);
            if (payment == null) {
                throw new RuntimeException("Payment not found for transaction ID: " + transactionId);
            }
            // Mark payment and order as completed
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
    
            Order order = payment.getOrder();
            order.setOrderStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        }
    
        // You can handle failed events, etc., here too
        return "Webhook processed";
    }
    
}
