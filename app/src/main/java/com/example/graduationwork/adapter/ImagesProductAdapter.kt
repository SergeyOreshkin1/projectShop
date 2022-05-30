package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ItemProductImageBinding


class ImagesProductAdapter(
    private val images: MutableList<String>
) : RecyclerView.Adapter<ImagesProductAdapter.ImagesViewHolder>() {

    override fun getItemCount(): Int = Integer.MAX_VALUE

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagesViewHolder =
        ImagesViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        if (images.isNotEmpty()) {
            val loopPos = position % images.size
            (holder as? ImagesViewHolder)?.bind(images[loopPos])
        }
    }

    inner class ImagesViewHolder(private val itemBinding: ItemProductImageBinding) : RecyclerView
    .ViewHolder(itemBinding.root) {
        fun bind(image: String) = with(itemBinding) {
            rvProductImage.load(image) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }
}
