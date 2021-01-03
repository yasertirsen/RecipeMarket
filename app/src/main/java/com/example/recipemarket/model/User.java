package com.example.recipemarket.model;

public class User {
    private String email;
    private String password;
    private String username;
    private String address;

    public User(String email, String password, String username, String address) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.address = address;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
