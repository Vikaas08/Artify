package com.example.hw4_cs571_spring_25;

import com.google.gson.annotations.SerializedName;

public class ArtistDetailsResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("deathday")
    private String deathday;
    @SerializedName("nationality")
    private String nationality;
    @SerializedName("biography")
    private String biography;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getBirthday() {
        return birthday;
    }
    public String getDeathday() {
        return deathday;
    }
    public String getNationality() {
        return nationality;
    }
    public String getBiography() {
        return biography;
    }
}
