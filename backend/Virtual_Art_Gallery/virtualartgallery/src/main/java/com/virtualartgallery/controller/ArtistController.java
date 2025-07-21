package com.virtualartgallery.controller;

import com.virtualartgallery.dto.ArtistDto;
import com.virtualartgallery.service.ArtistService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createArtist( @Valid @RequestBody ArtistDto artistDto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
    String loggedInArtist = userDetails.getUsername();  // Get the logged-in user's username
    artistService.createArtist(artistDto, loggedInArtist);
    return ResponseEntity.ok("Artist profile created successfully!");
    }

    // Get artist details
    @GetMapping("/{userId}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long userId) {
        return ResponseEntity.ok(artistService.getArtistDetails(userId));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ArtistDto> updateArtist(@PathVariable Long userId, @Valid @RequestBody ArtistDto ArtistDto) {
        ArtistDto updatedArtist = artistService.updateArtist(userId, ArtistDto);
        return ResponseEntity.ok(updatedArtist);
    }
    

}
