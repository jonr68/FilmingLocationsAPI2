package com.example.FilmingLacationsAPI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity // Marks this class as a JPA entity (a table in the DB)
public class ApiPayload {

    @Id // Marks this field as the primary key
    private String id;
    private String userName;
    private String locName;
    private String latLong;
    private String address;
    private String description;
    private String tag;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    public ApiPayload() {
    } // JPA requires a no-arg constructor

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters and setters (required for JPA unless you use field access + Lombok)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
    
    public void addImage(Image image) {
        images.add(image);
        image.setLocation(this);
    }
    
    public void removeImage(Image image) {
        images.remove(image);
        image.setLocation(null);
    }
}