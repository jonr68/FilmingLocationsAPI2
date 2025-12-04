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
    private final ImageRepository imageRepository;

    @Autowired
    public LocationController(ApiPayloadRepository repository, ImageRepository imageRepository) {
        this.repository = repository;
        this.imageRepository = imageRepository;
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
        // Get the first image for backwards compatibility
        List<Image> images = imageRepository.findByLocationId(id);
        
        if (images.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No images found for location id: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(images.get(0).getImageData());
    }
    
    @GetMapping("/location/{id}/images")
    public ResponseEntity<List<Map<String, String>>> getImages(@PathVariable String id) {
        List<Image> images = imageRepository.findByLocationId(id);
        
        if (images.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No images found for location id: " + id);
        }
        
        List<Map<String, String>> imageList = images.stream()
                .map(img -> Map.of("id", img.getId(), "url", "/location/" + id + "/image/" + img.getId()))
                .toList();
        
        return ResponseEntity.ok(imageList);
    }
    
    @GetMapping("/location/{locationId}/image/{imageId}")
    public ResponseEntity<byte[]> getImageById(@PathVariable String locationId, @PathVariable String imageId) {
        Image image = imageRepository.findByIdWithLocation(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found with id: " + imageId));
        
        if (!image.getLocation().getId().equals(locationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image does not belong to this location");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getImageData());
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
            @RequestParam(value = "images", required = false) MultipartFile[] imageFiles,
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
            if (imageFiles == null || imageFiles.length == 0) {
                return ResponseEntity.status(422).body(Map.of("error", "At least one image file is required"));
            }

            ApiPayload payload = new ApiPayload();
            payload.setUserName(userName);
            payload.setLatLong(latLong);
            payload.setAddress(address);
            payload.setDescription(description);
            payload.setTag(tag);

            ApiPayload saved = repository.save(payload);
            
            // Save all images
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    Image image = new Image();
                    image.setImageData(imageFile.getBytes());
                    saved.addImage(image);
                    imageRepository.save(image);
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process image: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(422).body(Map.of("error", "Failed to create entry: " + e.getMessage()));
        }
    }
    
    @PostMapping(value = "/location/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addImageToLocation(
            @PathVariable String id,
            @RequestParam(value = "image", required = true) MultipartFile imageFile) {

        try {
            ApiPayload location = repository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id));

            if (imageFile.isEmpty()) {
                return ResponseEntity.status(422).body(Map.of("error", "Image file is required"));
            }

            Image image = new Image();
            image.setImageData(imageFile.getBytes());
            location.addImage(image);
            imageRepository.save(image);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Image added successfully",
                    "imageId", image.getId()
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process image: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(422).body(Map.of("error", "Failed to add image: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/location/{locationId}/image/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable String locationId, @PathVariable String imageId) {
        Image image = imageRepository.findByIdWithLocation(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found with id: " + imageId));
        
        if (!image.getLocation().getId().equals(locationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image does not belong to this location");
        }
        
        imageRepository.delete(image);
        return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
    }
}