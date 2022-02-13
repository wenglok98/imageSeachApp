package com.example.imageapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.imageapp.Other.Converter

@Database(
    entities = [Image::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class ImageSearchDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
}