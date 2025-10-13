package com.example.hw4_cs571_spring_25;

public class LoginResponse {
    private String token;
    private String id;
    private String email;
    private String fullname;
    private String profileImageUrl;

    public String getToken() { return token; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
