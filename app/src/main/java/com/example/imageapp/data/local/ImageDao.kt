package com.example.imageapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Query("SELECT * FROM image_table WHERE keyword = :keyword")
    fun observeAllShoppingItems(keyword: String): LiveData<List<Image>>

    @Query("SELECT * FROM image_table WHERE keyword = :keyword ORDER BY `order`")
    suspend fun getImages(keyword: String): List<Image>

    @Query("SELECT EXISTS(SELECT * FROM image_table WHERE keyword = :keyword)")
    suspend fun hasImageCache(keyword: String): Boolean


}