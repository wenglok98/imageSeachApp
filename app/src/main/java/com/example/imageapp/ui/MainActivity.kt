package com.example.imageapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
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

    //Load More Image
    private var isLoadingMoreImage = false

    private val searchImageChannel = Channel<String>(Channel.CONFLATED)

    private var imageList = arrayListOf<Image>()
    private lateinit var imageAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.initChannel(main_View)
        setupAdapter()
        setupSearchListener()
        addScrollerListener()

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

                showResults()

            }

        }

        viewModel._loadingLoadMore.observe(this) {
            if (it) {
                showLoadMore()
            } else {
                hideLoadMore()
                isLoadingMoreImage = false
            }
        }
        viewModel.loadMoreImageList.observe(this) {
            if (it.isNotEmpty()) {
                Log.d("testlog", "the loaded ijmage once")
                imageList.addAll(it)
                imageAdapter.notifyDataSetChanged()
//                imageAdapter.notifyDataSetChanged()
            }
            isLoadingMoreImage = false
        }
    }


    private fun setupSearchListener() {
        search_View.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                showLoading()
                searchImageChannel.trySend(newText.trim())
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                showLoading()
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
            this, imageList, ::loadImage
        ).withStartPosition(position)
            .withTransitionFrom(imageView)
            .show()
    }

    private fun loadImage(imageView: ImageView, img: Image?) {
        var file = img?.imageBase64 ?: img?.url
        Glide.with(this).load(file).override(Target.SIZE_ORIGINAL).into(imageView)
    }

    private fun showLoading() {
        lottie_Loading.visibility = View.VISIBLE
        image_Recycler.visibility = View.GONE
    }

    private fun showResults() {
        lottie_Loading.visibility = View.GONE
        image_Recycler.visibility = View.VISIBLE
    }

    private fun showLoadMore() {
        lottie_load_more.visibility = View.VISIBLE
    }

    private fun hideLoadMore() {
        lottie_load_more.visibility = View.GONE

    }

    private fun addScrollerListener() {

        var layoutManager = image_Recycler.layoutManager as StaggeredGridLayoutManager
        //attaches scrollListener with RecyclerView
        image_Recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoadingMoreImage) {
                    //total no. of items
                    var totalItemCount = layoutManager.itemCount
                    //last visible item position
                    val lastVisibleItemPositions: IntArray =
                        layoutManager.findLastVisibleItemPositions(null)

                    var lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                    if (!isLoadingMoreImage && lastVisibleItemPosition > totalItemCount - 5) {

                        isLoadingMoreImage = true
                        viewModel.loadMoreImage()

                    }
                }
            }
        })
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }
}
