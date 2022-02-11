package com.example.imageapp.data

import com.example.imageapp.data.remote.ImageResult

data class ImageResponse(
    val _type :String,
    val totalCount : Int,
    val value : ArrayList<ImageResult>
)