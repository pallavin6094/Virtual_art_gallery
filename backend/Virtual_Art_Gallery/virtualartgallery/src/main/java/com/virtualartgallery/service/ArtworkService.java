package com.virtualartgallery.service;

import com.virtualartgallery.dto.ArtworkDto;
import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.repository.ArtistRepository;
import com.virtualartgallery.repository.ArtworkRepository;
import com.virtualartgallery.entity.User;
import com.virtualartgallery.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public ArtworkService(ArtworkRepository artworkRepository, UserRepository userRepository,ArtistRepository artistRepository) {
        this.artworkRepository = artworkRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    public Artwork uploadArtwork(Artwork artwork, String artistUsername) {
        User artistUser = userRepository.findByUsername(artistUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Artist not found"));

        if (!artistUser.getRole().name().equals("ARTIST")) {
            throw new AccessDeniedException("Only artists can upload artworks.");
        }

        // Fetch the corresponding Artist entity
    Artist artist = artistRepository.findByUser(artistUser)
            .orElseThrow(() -> new RuntimeException("Artist profile not found"));

    artwork.setArtist(artist); // ✅ Set the correct Artist entity
    return artworkRepository.save(artwork);
    }

    public Artwork editArtwork(Long id, Artwork updatedArtwork, String artistUsername) {
        Artwork existingArtwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        if (!existingArtwork.getArtist().getUser().getUsername().equals(artistUsername)) {
            throw new AccessDeniedException("You can only update your own artworks.");
        }

        existingArtwork.setTitle(updatedArtwork.getTitle());
        existingArtwork.setDescription(updatedArtwork.getDescription());
        existingArtwork.setPrice(updatedArtwork.getPrice());
        existingArtwork.setCategory(updatedArtwork.getCategory());
        existingArtwork.setImageUrl(updatedArtwork.getImageUrl());

        return artworkRepository.save(existingArtwork);
    }

    public void deleteArtworkById(Long id, String artistUsername) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        if (!artwork.getArtist().getUser().getUsername().equals(artistUsername)) {
            throw new AccessDeniedException("You can only delete your own artworks.");
        }

        artworkRepository.deleteById(id);
    }

    @Transactional
    public void deleteArtworkByTitle(String title, String artistUsername) {
        User user = userRepository.findByUsername(artistUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Artist not found"));
    
        Artist artist = artistRepository.findByUser(user)  // ✅ Get the correct Artist entity
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
    
        Optional<Artwork> artwork = artworkRepository.findByTitleAndArtist(title, artist);  // ✅ Now using Artist
        if (artwork.isEmpty()) {
            throw new RuntimeException("Artwork not found");
        }
    
        artworkRepository.deleteByTitleAndArtist(title, artist);
    }
    

    public List<Artwork> getArtistArtworks(String artistUsername) {
        User user = userRepository.findByUsername(artistUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
    
        return artworkRepository.findByArtist(artist);
    }
    
    public BigDecimal getSalesReport(String artistUsername) {
        User user = userRepository.findByUsername(artistUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
    
        List<Artwork> artworks = artworkRepository.findByArtist(artist);
    
        return artworks.stream()
            .map(Artwork::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Artwork> getArtworksByArtistId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        Artist artist = artistRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
    
        return artworkRepository.findByArtist(artist);
    }
    public List<ArtworkDto> getAllArtworks() {
        //return artworkRepository.findAll();
        List<Artwork> artworks = artworkRepository.findAll();
    return artworks.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    private ArtworkDto convertToDto(Artwork artwork) {
    ArtworkDto dto = new ArtworkDto();
    dto.setId(artwork.getId());
    dto.setArtistId(artwork.getArtist() != null ? artwork.getArtist().getId() : null);
    dto.setTitle(artwork.getTitle());
    dto.setDescription(artwork.getDescription());
    dto.setPrice(artwork.getPrice());
    dto.setCategory(artwork.getCategory());
    dto.setImageUrl(artwork.getImageUrl());
    dto.setStatus(artwork.getStatus().name());

    if (artwork.getArtist() != null && artwork.getArtist().getUser() != null) {
        dto.setArtistName(artwork.getArtist().getUser().getUsername());
        dto.setArtistRating(artwork.getArtist().getRating());
        dto.setArtistSpecialization(artwork.getArtist().getSpecialization());
    }

    return dto;
}

    

}    