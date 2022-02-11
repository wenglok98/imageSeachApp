package com.example.imageapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ImageSearchViewModel by viewModels()

    private val searchImageChannel = Channel<String>(Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSearchListener()

    }

    private fun setupSearchListener() {
        search_View.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                searchImageChannel.offer(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })

        lifecycleScope.launch(Dispatchers.IO) {
            searchImageChannel.consumeAsFlow()
                .debounce(500)
                .map { keyWord ->
                    viewModel.searchImage(keyWord, 1, PAGE_SIZE)
                }.collect()
        }
    }


}