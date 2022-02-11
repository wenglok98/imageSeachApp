package com.example.imageapp.repositories

import com.example.imageapp.Other.Resource
import com.example.imageapp.data.ImageResponse
import com.example.imageapp.data.local.ImageDao
import com.example.imageapp.data.remote.ImageApi
import retrofit2.Response
import javax.inject.Inject

class ImageSearchRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val imageApi: ImageApi
) : ImageRepo {
    override suspend fun searchImageFromApi(
        queryString: String,
        pageNumber: Int,
        pageSize: Int
    ): Resource<ImageResponse> {
        return try {
            val response = imageApi.searchImage(queryString, pageNumber, pageSize)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("An unknown error", null)
            } else {
                Resource.error("An unknown error", null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't reach the server.", null)
        }

    }

}