package com.example.imageapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {

    @GET("/api/Search/ImageSearchAPI")
    suspend fun searchImage(
        @Query("q") keyword : String,
        @Query("pageNumber") pageNumber : Int,
        @Query("pageSize") pageSize : Int
    )
}