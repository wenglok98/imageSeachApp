package com.example.imageapp.data.remote

import com.example.imageapp.Other.Constants
import com.example.imageapp.data.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ImageApi {

    @Headers(
        "x-rapidapi-host: ${Constants.API_HOST}",
        "x-rapidapi-key: ${Constants.API_KEY}"
    )
    @GET("/api/Search/ImageSearchAPI")
    suspend fun searchImage(
        @Query("q") keyword: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ImageResponse>
}