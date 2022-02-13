package com.example.imageapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.imageapp.Other.Constants.PAGE_SIZE
import com.example.imageapp.viewmodel.ImageSearchViewModel
import com.example.imageapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ImageSearchViewModel by viewModels()

    private val searchImageChannel = Channel<String>(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.initChannel(main_View)
        setupSearchListener()
        setImageData()
    }


    private fun setupSearchListener() {
        search_View.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                searchImageChannel.trySend(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchImageChannel.trySend(query)
                return false
            }

        })

        lifecycleScope.launch(Dispatchers.IO) {
            searchImageChannel.consumeAsFlow()
                .debounce(500)
                .map { keyWord ->

                    viewModel.searchImageFromLocal(keyWord)

                }.collect()
        }
    }

    private fun setImageData() {
        viewModel.imageList.observe(this) {
            if (it.isNotEmpty()) {
                var image = it[it.size - 1].imageBase64 ?: it[it.size - 1].url
                Glide.with(applicationContext).load(image).into(testim)
            }

        }
    }


}