package com.example.FilmingLacationsAPI;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

// JPA Entity
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


// Controller
@RestController
public class LocationController {
    private final ApiPayloadRepository repository;

    @Autowired
    public LocationController(ApiPayloadRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/location")
    public Map<String, String> index() {
        // Implement or remove this method as needed
        return Map.of("message", "Location endpoint");
    }

    @GetMapping("/locations")
    public List<ApiPayload> getAllLocations() {
        return repository.findAll();
    }


    @PostMapping("/location")
    public ApiPayload create(@RequestBody ApiPayload payload) {
        return repository.save(payload);
    }
}
///test