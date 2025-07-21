package com.virtualartgallery.service;

import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.Payment;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.exception.ResourceNotFoundException;
import com.virtualartgallery.dto.OrderResponseDto;
import com.virtualartgallery.dto.PurchasedArtworkDto;
import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.ArtworkStatus;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.CartItem;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.repository.ArtistRepository;
import com.virtualartgallery.repository.ArtworkRepository;
import com.virtualartgallery.repository.BuyerRepository;
import com.virtualartgallery.repository.CartItemRepository;
import com.virtualartgallery.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ArtworkRepository artworkRepository;
    private final PaymentRepository paymentRepository;
    private final BuyerRepository buyerRepository;
    private final ArtistRepository artistRepository;
    private final CartItemRepository cartItemRepository;
    

    public OrderService(OrderRepository orderRepository, ArtworkRepository artworkRepository, PaymentRepository paymentRepository,BuyerRepository buyerRepository,ArtistRepository artistRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.artworkRepository = artworkRepository;
        this.paymentRepository = paymentRepository;
        this.buyerRepository = buyerRepository;
        this.artistRepository = artistRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
public Order placeOrder(Long artworkId, String buyerUsername) {
    Artwork artwork = artworkRepository.findById(artworkId)
            .orElseThrow(() -> new RuntimeException("Artwork not found"));

    Buyer buyer = buyerRepository.findByUser_Username(buyerUsername)
            .orElseThrow(() -> new RuntimeException("Buyer not found"));

    Order order = new Order();
    order.setArtwork(artwork);
    order.setBuyer(buyer);
    order.setTotalPrice(artwork.getPrice());
    order.setOrderDate(LocalDateTime.now());
    return orderRepository.save(order); // ✅ Just save the order
}

   @Transactional
public Order checkoutSingleItem(Long cartItemId, String buyerUsername) {
    Buyer buyer = buyerRepository.findByUser_Username(buyerUsername)
            .orElseThrow(() -> new RuntimeException("Buyer not found"));

    CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

    Artwork artwork = cartItem.getArtwork();
    if (artwork.getStatus() == ArtworkStatus.SOLD) {
        throw new IllegalStateException("You can't order this artwork as it is already sold.");
    }
    artwork.setStatus(ArtworkStatus.SOLD);
    artworkRepository.save(artwork); 

    // ✅ Create Order for the selected cart item
    Order order = new Order();
    order.setArtwork(artwork);
    order.setBuyer(buyer);
    order.setTotalPrice(artwork.getPrice());
    order.setOrderDate(LocalDateTime.now());
    Order savedOrder = orderRepository.save(order);  // ✅ Return saved order

    orderRepository.save(order);

    // ✅ Remove only the selected item from the cart
    cartItemRepository.delete(cartItem);

    return savedOrder;  // ✅ Return the created order
}

    // ✅ Complete Payment (Now Updates `Payment` Entity)
    @Transactional
   public void completePayment(Long orderId, String transactionId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    Payment payment = paymentRepository.findByOrder(order)
            .orElseThrow(() -> new RuntimeException("Payment record not found"));

    // ✅ Automatically get the amount from the order
    BigDecimal amount = order.getTotalPrice();
    payment.setStatus(PaymentStatus.COMPLETED);
    payment.setTransactionId(transactionId);
    payment.setAmount(amount);  // Set the amount directly

    // ✅ Update Order Status to COMPLETED
    order.setOrderStatus(OrderStatus.COMPLETED);
    orderRepository.save(order);

    // ✅ Print the transaction ID for confirmation
    System.out.println("Transaction ID: " + payment.getTransactionId());

    paymentRepository.save(payment);
}


    // ✅ Handle Failed Payment
    @Transactional
    public void failPayment(Long orderId, String transactionId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        Payment payment = paymentRepository.findByOrder(order)
        .orElseThrow(() -> new RuntimeException("Payment not found for Order ID: " + orderId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setTransactionId(transactionId);

        // ✅ Update Order Status to CANCELLED
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        paymentRepository.save(payment);
    }

    public List<OrderResponseDto> getOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderResponseDto(
                        order.getId(),
                        order.getArtwork().getTitle(),
                        order.getBuyer().getUser().getUsername(), // Ensure this is included if required
                        order.getTotalPrice()   // Directly pass BigDecimal
                ))
                .collect(Collectors.toList());
    }

    public List<OrderResponseDto> getOrdersByBuyer(String buyerUsername) {
        Buyer buyer = buyerRepository.findByUser_Username(buyerUsername)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
    
        List<Order> orders = orderRepository.findByBuyer(buyer);
    
        return orders.stream()
                .map(order -> new OrderResponseDto(
                        order.getId(),
                        order.getArtwork().getTitle(),
                        order.getBuyer().getUser().getUsername(),
                        order.getTotalPrice()
                ))
                .collect(Collectors.toList());
    }

    // ✅ Get artist earnings
    public BigDecimal getArtistEarnings(String username) {
        Artist artist = artistRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        List<Order> orders = orderRepository.findByArtwork_Artist(artist);

        // ✅ Sum total earnings from all sold artworks
        return orders.stream()
                .map(Order::getTotalPrice) // Extract price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum all prices
    }
   public List<PurchasedArtworkDto> getPurchasedArtworksForBuyer(Long buyerId) {
    Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new RuntimeException("Buyer not found"));

    return orderRepository.findByBuyer(buyer).stream()
            .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETED &&
                             order.getPayment() != null &&
                             order.getPayment().getStatus() == PaymentStatus.COMPLETED)
            .map(order -> {
                Artwork artwork = order.getArtwork();
                LocalDateTime purchaseDate = order.getPayment().getPurchaseDate();
                return new PurchasedArtworkDto(
                        artwork.getId(),
                        artwork.getTitle(),
                        artwork.getImageUrl(),
                        purchaseDate
                );
            })
            .collect(Collectors.toList());
}


    
    
    
}
