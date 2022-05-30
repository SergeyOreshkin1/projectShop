package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationwork.databinding.ItemBottomSheetBinding
import com.example.graduationwork.data.entity.Type


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Type>() {
    override fun areItemsTheSame(oldItem: Type, newItem: Type): Boolean =
        oldItem.value == newItem.value

    override fun areContentsTheSame(oldItem: Type, newItem: Type):
        Boolean = oldItem == newItem
}

class TypeAdapter() : RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {
    var listener: (Type) -> Unit = {}
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun submitList(size: MutableList<Type>) {
        differ.submitList(size)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypeViewHolder =
        TypeViewHolder(
            ItemBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.item.setOnClickListener {
            listener(differ.currentList[position])
        }
    }

    inner class TypeViewHolder(private val itemBinding: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(size: Type) = with(itemBinding) {
            tvItem.text = size.value
        }

        val item = itemView
    }
}
