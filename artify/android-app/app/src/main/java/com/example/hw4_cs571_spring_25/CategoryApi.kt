package com.example.hw4_cs571_spring_25

import retrofit2.http.Body
import retrofit2.http.POST

interface CategoryApi {
    @POST("category")
    suspend fun getCategories(@Body request: CategoryRequest): CategoryResponse
}
