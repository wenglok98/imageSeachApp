package com.example.imageapp.repositories

import androidx.lifecycle.LiveData
import com.example.imageapp.Other.Resource
import com.example.imageapp.data.ImageResponse
import com.example.imageapp.data.local.Image

interface ImageRepo {

    suspend fun insertImage(images: Image)

    suspend fun getLiveImage(keyword: String): LiveData<ArrayList<Image>>

    suspend fun hasImageCache(keyword: String): Boolean

    suspend fun getLastOrder(keyword: String): Int

    suspend fun getImages(keyword: String): ArrayList<Image>

    suspend fun searchImageFromApi(
        queryString: String,
        pageNumber: Int,
        size: Int
    ): Resource<ImageResponse>

}