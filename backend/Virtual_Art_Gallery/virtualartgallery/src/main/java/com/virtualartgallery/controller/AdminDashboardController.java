package com.virtualartgallery.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtualartgallery.dto.UserDto;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // user access for admin
    @GetMapping("/users")
     public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = adminDashboardService.getAllUsers();
        return ResponseEntity.ok(users);
}


    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminDashboardService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // order access for admin
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = adminDashboardService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{orderId}/status/{status}")
public ResponseEntity<String> updateOrderStatus( @PathVariable Long orderId, @PathVariable OrderStatus status) {

    adminDashboardService.updateOrderStatus(orderId, status);
    return ResponseEntity.ok("Order status updated to " + status);
}


    // artwork access for admin
    @GetMapping("/artworks")
    public ResponseEntity<List<Artwork>> getAllArtworks() {
        List<Artwork> artworks = adminDashboardService.getAllArtworks();
        return ResponseEntity.ok(artworks);
    }

    @DeleteMapping("/artworks/{artworkId}")
    public ResponseEntity<String> deleteArtwork(@PathVariable Long artworkId) {
         adminDashboardService.deleteArtwork(artworkId);
        return ResponseEntity.ok("Artwork deleted successfully");
    }

}
