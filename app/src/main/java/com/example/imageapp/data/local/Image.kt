package com.example.imageapp.data.local

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class Image(
    var thumbnail: String,
    var url: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)