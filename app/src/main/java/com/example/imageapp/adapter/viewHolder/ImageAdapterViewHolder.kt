package com.example.imageapp.adapter.viewHolder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.imageapp.adapter.ImageAdapter
import com.example.imageapp.data.local.Image
import kotlinx.android.synthetic.main.image_adapter_layout.view.*

class ImageAdapterViewHolder
constructor(
    itemView: View,
    private val interaction: ImageAdapter.Interaction?
) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Image) = with(itemView) {

        try {
            var image = item.imageBase64 ?: item.thumbnail

            image?.let {
                Glide.with(context).load(image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(Target.SIZE_ORIGINAL).into(imageView_im)
            }
        } catch (e: Exception) {
        }

        itemView.setOnClickListener {
            interaction?.onItemSelected(adapterPosition, item)
        }


    }
}