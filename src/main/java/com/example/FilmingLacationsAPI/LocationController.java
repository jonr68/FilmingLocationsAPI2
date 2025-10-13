package com.example.FilmingLacationsAPI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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

    @GetMapping("/location/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        ApiPayload payload = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        if (payload.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or IMAGE_PNG
                .body(payload.getImage());
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

    //trying to fix master branch with new branch
    @PostMapping("/upload")
    public ResponseEntity<?> createEntry(
            @RequestParam("username") String username,
            @RequestParam("latLong") String latLong,
            @RequestParam("address") String address,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag) throws IOException {

        ApiPayload payload = new ApiPayload();
        payload.setUsername(username);
        payload.setLatLong(latLong);
        payload.setAddress(address);
        payload.setImage(imageFile.getBytes()); // Convert MultipartFile to byte[]
        payload.setDescription(description);
        payload.setTag(tag);

        // Save to repository
        repository.save(payload);

        return ResponseEntity.ok(payload);
    }

}
