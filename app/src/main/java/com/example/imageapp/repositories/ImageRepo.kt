package com.example.imageapp.repositories

import com.example.imageapp.Other.Resource
import com.example.imageapp.data.ImageResponse

interface ImageRepo {

    suspend fun searchImageFromApi(queryString: String,pageNumber:Int,size:Int): Resource<ImageResponse>

}