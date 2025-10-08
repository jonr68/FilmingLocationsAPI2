package com.example.FilmingLacationsAPI;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity // Marks this class as a JPA entity (a table in the DB)
public class ApiPayload {

    @Id // Marks this field as the primary key
    private String id;
    private String username;
    private String latLong;
    private String address;
    private String image;
    private String description;
    private String tag;

    public ApiPayload() {} // JPA requires a no-arg constructor

    // Getters and setters (required for JPA unless you use field access + Lombok)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getLatLong() { return latLong; }
    public void setLatLong(String latLong) { this.latLong = latLong; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}