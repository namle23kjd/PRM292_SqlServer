package com.example.prm292_sqlserver.Model;

public class User {
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
    private int roleId;
    private String roleName; // This will be populated when included with Role data

    // Default constructor
    public User() {
    }

    // Constructor for creating new users (without ID)
    public User(String name, String email, String phoneNumber, String address, String dateOfBirth, int roleId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.roleId = roleId;
    }

    // Full constructor
    public User(int id, String name, String email, String phoneNumber, String address, String dateOfBirth, int roleId, String roleName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // ========== GETTERS ==========

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    // ========== SETTERS ==========

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // ========== UTILITY METHODS ==========

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }

    // Helper method to check if user has valid data
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                roleId > 0;
    }

    // Helper method to get display name
    public String getDisplayName() {
        return name != null ? name : "Unknown User";
    }

    // Helper method to get formatted email
    public String getFormattedEmail() {
        return email != null ? email.toLowerCase() : "";
    }
}