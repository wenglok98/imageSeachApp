package com.example.imageapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.annotation.UiContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.imageapp.Other.Constants.PAGE_SIZE
import com.example.imageapp.data.local.Image
import com.example.imageapp.repositories.ImageSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val repository: ImageSearchRepository
) : ViewModel() {

    private val _imageList = MutableLiveData<ArrayList<Image>>()
    var imageList: LiveData<ArrayList<Image>> = _imageList

    //Channel to update base64 for image
    private val processBase64Channel = Channel<Image>(Channel.UNLIMITED)

    suspend fun getLiveImages(keyword: String) = repository.getLiveImage(keyword)

    fun searchImageFromLocal(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var cacheExist = repository.hasImageCache(keyword)
            if (cacheExist) {
                var imagesDb = repository.getImages(keyword)
                _imageList.postValue(imagesDb)
            } else {
                searchImage(keyword, 1, PAGE_SIZE)
            }
        }
    }

    private fun searchImage(keyword: String, pageNumber: Int, pageSize: Int) {
        var order = 0
        var tempList = arrayListOf<Image>()
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchImageFromApi(
                keyword,
                pageNumber,
                pageSize
            ).data?.value?.forEach { imageResult ->
                try {
                    var tempImage: Image = Image(
                        imageResult.thumbnail,
                        imageResult.url,
                        id = UUID.randomUUID().toString(),
                        keyword = keyword,
                        order = order
                    )
                    order++
                    tempList.add(tempImage)
                    processBase64Channel.trySend(tempImage)
                } catch (e: Exception) {
                }

            }
            _imageList.postValue(tempList)
        }
    }

    fun initChannel(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            processBase64Channel.consumeAsFlow().map { image ->
                launch {
                    try {
                        var tempBitmap =
                            Glide.with(view).asBitmap().override(600, 600).load(image.url).submit()
                                .get()
                        repository.insertImage(image.apply {
                            this.imageBase64 = tempBitmap
                        })
                    } catch (e: Exception) {
                    }

                }
            }.collect()
        }
    }
}