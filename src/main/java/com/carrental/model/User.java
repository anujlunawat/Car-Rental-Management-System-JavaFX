package com.carrental.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

@Entity("users")
public class User {
    @Id
    private ObjectId id;
    
    @Property("username")
    private String username;
    
    @Property("password")
    private String password;
    
    @Property("fullName")
    private String fullName;
    
    @Property("email")
    private String email;
    
    @Property("phone")
    private String phone;
    
    @Property("address")
    private String address;
    
    @Property("role")
    private UserRole role;
    
    public enum UserRole {
        USER,
        ADMIN,
        CUSTOMER // Keep CUSTOMER for backward compatibility
    }
    
    // Default constructor
    public User() {}
    
    // Constructor with fields
    public User(String username, String password, String fullName, String email, String phone, String address, UserRole role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }
    
    // Getters and Setters
    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    // Helper method to check if user is a customer (either USER or CUSTOMER role)
    public boolean isCustomer() {
        return role == UserRole.USER || role == UserRole.CUSTOMER;
    }
} 