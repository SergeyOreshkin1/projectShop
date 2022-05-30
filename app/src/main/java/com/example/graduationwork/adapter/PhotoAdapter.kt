package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationwork.databinding.ItemBottomSheetBinding
import com.example.graduationwork.data.entity.Photo


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem == newItem
}

class PhotoAdapter() : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    var listener: (Photo) -> Unit = {}
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun submitList(photo: List<Photo>) {
        differ.submitList(photo)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder =
        PhotoViewHolder(
            ItemBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.item.setOnClickListener {
            listener(differ.currentList[position])
        }
    }

    inner class PhotoViewHolder(private val itemBinding: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(photo: Photo) = with(itemBinding) {
            tvItem.text = photo.title
        }

        val item = itemView
    }
}
