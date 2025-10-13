package com.example.hw4_cs571_spring_25;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SearchApi {
    @GET("search")
    Call<SearchResponse> searchArtists(@Query("userInput") String query);

    @POST("about")
    Call<ArtistDetailsResponse> getArtistDetails(@Body ArtistRequest request);

    @POST("similar")
    Call<SimilarArtistResponse> getSimilarArtists(@Body ArtistRequest request);

    @POST("artworks")
    Call<ArtworkResponse> getArtworks(@Body ArtistRequest request);

//    @POST("category")
//    Call<CategoryResponse> getCategories(@Body CategoryRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<String> register(@Body RegisterRequest request);

    @POST("delete")
    Call<DeleteResponse> deleteAccount(@Body DeleteRequest request);

}
