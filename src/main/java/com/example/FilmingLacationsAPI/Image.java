package com.example.FilmingLacationsAPI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "images")
public class Image {

    @Id
    private String id;

    @Column(name = "image_data", columnDefinition = "BLOB")
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnore
    private ApiPayload location;

    public Image() {
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public ApiPayload getLocation() {
        return location;
    }

    public void setLocation(ApiPayload location) {
        this.location = location;
    }
}
