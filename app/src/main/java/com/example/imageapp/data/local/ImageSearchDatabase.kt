package com.example.imageapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Image::class],
    version = 1
)
abstract class ImageSearchDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
}