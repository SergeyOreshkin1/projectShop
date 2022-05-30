package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.R


class ViewPagerAdapter(
    private val imagesList: List<String>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_image_vp, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        if (imagesList.isNotEmpty()) {
            val loopPos = position % imagesList.size
            (holder as? ViewPagerHolder)?.bind(imagesList[loopPos])
        }
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivSliderImage = itemView.findViewById<ImageView>(R.id.viewPager2Image)

        fun bind(image: String) {
            ivSliderImage.load(image) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }
}
