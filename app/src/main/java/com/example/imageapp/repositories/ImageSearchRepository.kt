package com.example.imageapp.repositories

import com.example.imageapp.data.local.ImageDao
import com.example.imageapp.data.remote.ImageApi
import javax.inject.Inject

class ImageSearchRepository @Inject constructor(
    private val imageDao: ImageDao,
    private val imageApi: ImageApi
):ImageRepo{

}