package com.virtualartgallery.service;

import com.virtualartgallery.dto.ArtistDto;
import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.User;
import com.virtualartgallery.exception.ResourceNotFoundException;
import com.virtualartgallery.repository.ArtistRepository;
import com.virtualartgallery.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public ArtistService(ArtistRepository artistRepository, UserRepository userRepository) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
    }

    public void createArtist(ArtistDto artistDto, String username) {
        // Find the user by email
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if the artist profile already exists
        if (artistRepository.existsById(user.getId())) {
            throw new RuntimeException("Artist profile already exists!");
        }

        // Create a new Artist entity
        Artist artist = new Artist();
        artist.setUser(user);
        artist.setBio(artistDto.getBio());
        artist.setSpecialization(artistDto.getSpecialization());
        artist.setExperience(artistDto.getExperience());
        artist.setEmail(artistDto.getEmail());
        artist.setLocation(artistDto.getLocation());
        artist.setProfileImage(artistDto.getProfileImage());
        artist.setRating(artistDto.getRating());
        artist.setPhoneNumber(artistDto.getPhoneNumber());

        // Save the artist profile
        artistRepository.save(artist);
    }
    // Get artist details
    public ArtistDto getArtistDetails(Long userId) {
        Artist artist = artistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

                return new ArtistDto(
                    artist.getId(),
                    artist.getBio(),
                    artist.getEmail(),
                    artist.getUser().getUsername(),
                    artist.getPhoneNumber(),
                    artist.getSpecialization(),
                    artist.getExperience(),
                    artist.getLocation(),
                    artist.getProfileImage(),
                    artist.getRating()
                );
    }
    

    public Long getArtistIdByEmail(String email) {
        return artistRepository.findByEmail(email)
                .map(Artist::getId) // Extracts the artist ID
                .orElseThrow(() -> new UsernameNotFoundException("Artist not found with email: " + email));
    }

    @Transactional
public ArtistDto updateArtist(Long userId, ArtistDto artistDto) {
    Artist artist = artistRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

    // ✅ Update artist fields
    // Update artist fields
    artist.setBio(artistDto.getBio());
    artist.setSpecialization(artistDto.getSpecialization());
    artist.setExperience(artistDto.getExperience());
    artist.setEmail(artistDto.getEmail());  // Consider whether email should be changed
    artist.setLocation(artistDto.getLocation());
    artist.setProfileImage(artistDto.getProfileImage());
    artist.setRating(artistDto.getRating());
    artist.setPhoneNumber(artistDto.getPhoneNumber());

    // ✅ Save updated artist
    Artist updatedArtist = artistRepository.save(artist);

    // ✅ Convert updated artist entity to ArtistDto and return
    return new ArtistDto(
        updatedArtist.getId(),
            updatedArtist.getBio(),
            updatedArtist.getEmail(),
            updatedArtist.getUser().getUsername(),
            updatedArtist.getPhoneNumber(),
            updatedArtist.getSpecialization(),
            updatedArtist.getExperience(),
            updatedArtist.getLocation(),
            updatedArtist.getProfileImage(),
            updatedArtist.getRating()
    );
}

}
