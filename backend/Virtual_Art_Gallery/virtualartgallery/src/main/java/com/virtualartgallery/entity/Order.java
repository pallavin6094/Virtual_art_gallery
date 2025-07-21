package com.virtualartgallery.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    @JsonIgnore
    private Artwork artwork;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonIgnore
    private Buyer buyer; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonBackReference
    private Payment payment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    private BigDecimal totalPrice;
}
