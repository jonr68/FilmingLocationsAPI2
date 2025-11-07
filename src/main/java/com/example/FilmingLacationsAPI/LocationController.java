package com.example.FilmingLacationsAPI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class LocationController {
    private final ApiPayloadRepository repository;

    @Autowired
    public LocationController(ApiPayloadRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/location")
    public Map<String, String> index() {
        return Map.of("message", "Location endpoint");
    }

    @GetMapping("/locations")
    public List<ApiPayload> getAllLocations() {
        return repository.findAll();
    }

    @GetMapping("/location/{id}")
    public ApiPayload getLocationById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id));
    }

    @GetMapping("/location/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        ApiPayload payload = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id));

        if (payload.getImage() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found for location id: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(payload.getImage());
    }

    @PostMapping("/location")
    public ApiPayload create(@RequestBody ApiPayload payload) {
        return repository.save(payload);
    }

    @DeleteMapping("/location/{id}")
    public Map<String, String> deleteLocationById(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id);
        }
        repository.deleteById(id);
        return Map.of("message", "Location deleted with id: " + id);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEntry(
            @RequestParam(value = "username", required = false) String userName,
            @RequestParam(value = "latLong", required = false) String latLong,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "tag", required = false) String tag) {

        try {
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Username is required"));
            }
            if (latLong == null || latLong.trim().isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "LatLong is required"));
            }
            if (address == null || address.trim().isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Address is required"));
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Description is required"));
            }
            if (tag == null || tag.trim().isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Tag is required"));
            }
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Image file is required"));
            }

            ApiPayload payload = new ApiPayload();
            payload.setUserName(userName);
            payload.setLatLong(latLong);
            payload.setAddress(address);
            payload.setImage(imageFile.getBytes());
            payload.setDescription(description);
            payload.setTag(tag);

            ApiPayload saved = repository.save(payload);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process image: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(422).body(Map.of("error", "Failed to create entry: " + e.getMessage()));
        }
    }
}