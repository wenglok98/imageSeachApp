package com.example.imageapp.di

import android.content.Context
import androidx.room.Room
import com.example.imageapp.Other.Constants.BASE_URL
import com.example.imageapp.Other.Constants.DATABASE_NAME
import com.example.imageapp.data.local.ImageDao
import com.example.imageapp.data.local.ImageSearchDatabase
import com.example.imageapp.data.remote.ImageApi
import com.example.imageapp.repositories.ImageSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {



    @Singleton
    @Provides
    fun provideImageSearchDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,ImageSearchDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideImageRepository(
        dao: ImageDao,
        api: ImageApi
    ) = ImageSearchRepository(dao, api)


    @Singleton
    @Provides
    fun provideImageDao(
        database: ImageSearchDatabase
    ) = database.imageDao()

    @Singleton
    @Provides
    fun provideImageSearchApi(): ImageApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ImageApi::class.java)
    }


}