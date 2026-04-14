package com.example.gosport.model;
public class User {

    private int id;
    private String role;
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private int isActive;
    private int isDeleted;
    private String createdAt;

    // ================= CONSTRUCTOR =================

    public User() {
    }

    public User(int id, String role, String fullName, String phone,
                String email, String password,
                int isActive, int isDeleted, String createdAt) {
        this.id = id;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    // ================= GETTER & SETTER =================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }


    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // ================= HELPER METHODS =================

    public boolean isActive() {
        return isActive == 1;
    }

    public boolean isDeleted() {
        return isDeleted == 1;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(role);
    }
}