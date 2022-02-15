package com.example.imageapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.imageapp.Other.Constants.PAGE_SIZE
import com.example.imageapp.viewmodel.ImageSearchViewModel
import com.example.imageapp.R
import com.example.imageapp.adapter.ImageAdapter
import com.example.imageapp.data.local.Image
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
class MainActivity : AppCompatActivity(), ImageAdapter.Interaction {
    private val viewModel: ImageSearchViewModel by viewModels()

    private val searchImageChannel = Channel<String>(Channel.CONFLATED)
    private var imageList = arrayListOf<Image>()
    private lateinit var imageAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.initChannel(main_View)
        setupAdapter()
        setupSearchListener()
    }

    private fun setupAdapter() {
        imageAdapter = ImageAdapter(this@MainActivity)
        image_Recycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = imageAdapter
            itemAnimator = null
        }
        imageAdapter.submitList(imageList)

        viewModel.imageList.observe(this) {
            if (it.isNotEmpty()) {
                imageList.clear()
                imageAdapter.notifyDataSetChanged()
                imageList.addAll(it)
                imageAdapter.notifyDataSetChanged()
            }

        }
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


    override fun onItemSelected(position: Int, item: Image) {
        TODO("STFALCON TO VIEW IMAGE")
    }


}