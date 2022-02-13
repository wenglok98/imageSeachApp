package com.example.imageapp.data.local

import android.graphics.Bitmap
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class Image(
    var thumbnail: String,
    var url: String,
    var keyword: String,
    var order: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var imageBase64: Bitmap? = null,
    @PrimaryKey(autoGenerate = false)
    val id: String
)