package com.example.imageapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
import com.google.android.material.imageview.ShapeableImageView
import com.stfalcon.imageviewer.StfalconImageViewer
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

    //ImageViewer
    private var stfalconImageViewer: StfalconImageViewer<Image>? = null

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
                searchImageChannel.trySend(newText.trim())
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchImageChannel.trySend(query.trim())
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


    override fun onItemSelected(imageView: ShapeableImageView, position: Int, item: Image) {
        stfalconImageViewer = StfalconImageViewer.Builder(
            this, viewModel.imageList.value, ::loadImage
        ).withStartPosition(position)
            .withTransitionFrom(imageView)
            .show()
    }

    private fun loadImage(imageView: ImageView, img: Image?) {
        var file = img?.imageBase64 ?: img?.url
        Glide.with(this).load(file).into(imageView)
    }


}