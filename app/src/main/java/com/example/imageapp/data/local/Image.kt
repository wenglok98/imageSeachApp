package com.example.imageapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_search")
data class Image(
    var thumbnail: String,
    var url: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)