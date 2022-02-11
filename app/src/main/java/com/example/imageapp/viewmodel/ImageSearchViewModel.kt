package com.example.imageapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageapp.data.local.Image
import com.example.imageapp.repositories.ImageSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val repository: ImageSearchRepository
) : ViewModel() {

    private val _imageList = MutableLiveData<ArrayList<Image>>()

    var imageList : LiveData<ArrayList<Image>> = _imageList

    fun searchImage(keyword: String, pageNumber: Int, pageSize: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchImageFromApi(keyword, pageNumber, pageSize).data?.value?.forEach { imageResult ->
                
            }

        }
    }
}