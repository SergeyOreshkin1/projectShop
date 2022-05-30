package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ItemProductBinding
import com.example.graduationwork.data.entity.Product

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product):
            Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Product, newItem: Product):
            Boolean = oldItem == newItem
}

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    var listener: (Product) -> Unit = {}
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun submitList(products: List<Product>) {
        differ.submitList(products)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.button.setOnClickListener {
            listener(differ.currentList[position])
        }
        holder.item.setOnClickListener {
            listener(differ.currentList[position])
        }
    }

    inner class ProductViewHolder(private val itemBinding: ItemProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(product: Product) = with(itemBinding) {
            textTitle.text = product.name
            textDescription.text = product.description
            val price = product.price.toString() + " ₽"
            textPrice.text = price

            imagePreview.load(product.image) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }

        val item = itemView
        val button = itemBinding.buttonBuy
    }
}
