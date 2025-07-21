package com.virtualartgallery.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long id;

    @Column(nullable = false, unique = true)  // Email should not be null if it's unique
    private String email;

    @Column(nullable = false, unique = true)  // Contact number should also be non-null
    private String contactNumber;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)  // ✅ Links to User
    private User user;

    @Column(nullable = true)  // Address can be null
    private String address;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;
}
