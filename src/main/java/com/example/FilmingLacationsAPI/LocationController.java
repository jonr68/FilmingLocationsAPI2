package com.example.FilmingLacationsAPI;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


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

    @GetMapping("/location/{id}")
    public ApiPayload getLocationById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
    }


    @PostMapping("/location")
    public ApiPayload create(@RequestBody ApiPayload payload) {
        return repository.save(payload);
    }

    @DeleteMapping("/location/{id}")
    public Map<String, String> deleteLocationById(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        repository.deleteById(id);
        return Map.of("message", "Location deleted with id: " + id);
    }

}
