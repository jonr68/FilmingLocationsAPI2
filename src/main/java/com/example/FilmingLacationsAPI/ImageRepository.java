package com.example.FilmingLacationsAPI;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    @Query("SELECT i FROM Image i WHERE i.location.id = :locationId")
    List<Image> findByLocationId(@Param("locationId") String locationId);
    
    @Query("SELECT i FROM Image i JOIN FETCH i.location WHERE i.id = :id")
    Optional<Image> findByIdWithLocation(@Param("id") String id);
}
