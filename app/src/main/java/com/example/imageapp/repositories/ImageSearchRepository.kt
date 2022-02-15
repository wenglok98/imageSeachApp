package com.example.imageapp.repositories

import androidx.lifecycle.LiveData
import com.example.imageapp.Other.Resource
import com.example.imageapp.data.ImageResponse
import com.example.imageapp.data.local.Image
import com.example.imageapp.data.local.ImageDao
import com.example.imageapp.data.remote.ImageApi
import retrofit2.Response
import javax.inject.Inject

class ImageSearchRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val imageApi: ImageApi
) : ImageRepo {

    override suspend fun insertImage(image: Image) {
        imageDao.insertImage(image)
    }

    override suspend fun getLiveImage(keyword: String): LiveData<ArrayList<Image>> {
        return imageDao.observeImageList(keyword) as LiveData<ArrayList<Image>>
    }

    override suspend fun hasImageCache(keyword: String): Boolean {
        return imageDao.hasImageCache(keyword)
    }

    override suspend fun getLastOrder(keyword: String): Int {
        return imageDao.getLastOrder(keyword)
    }

    override suspend fun getImages(keyword: String): ArrayList<Image> {
        return imageDao.getImages(keyword) as ArrayList<Image>
    }

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