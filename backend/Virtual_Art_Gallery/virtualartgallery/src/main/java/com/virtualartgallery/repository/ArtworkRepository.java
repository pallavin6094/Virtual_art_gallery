package com.virtualartgallery.repository;

import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    Optional<Artwork> findByTitle(String title);

    void deleteByTitle(String title);

    List<Artwork> findByArtist(Artist artist); 

    Optional<Artwork> findByTitleAndArtist(String title, Artist artist);
    
    void deleteByTitleAndArtist(String title, Artist artist);

    long countByCreatedAtAfter(LocalDateTime createAt);


}
