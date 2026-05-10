package com.vedology.model;

import java.sql.Timestamp;
import java.time.LocalTime;

public class User {
    private int userId;
    private String email;
    private String password;
    private String role;
    private Timestamp createdAt;
    private String fullName;
    private LocalTime timeOfBirth;
    private String phone;
    private String profileImage;


    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalTime getTimeOfBirth() { return timeOfBirth; }
    public void setTimeOfBirth(LocalTime timeOfBirth) { this.timeOfBirth = timeOfBirth; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}