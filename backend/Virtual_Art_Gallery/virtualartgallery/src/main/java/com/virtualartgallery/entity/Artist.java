package com.virtualartgallery.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    private Long id;  // Same as User ID

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String experience;

    @Column(nullable = false, unique = true)
    private String email;

    private String location;
    private String profileImage;
    private Double rating;
    private String phoneNumber;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Artwork> artworks;
    
    @OneToOne
    @MapsId  // Ensures ID is same as User ID
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}

