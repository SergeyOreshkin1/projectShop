package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ItemOrderBinding
import com.example.graduationwork.data.entity.Order


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order):
            Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Order, newItem: Order):
            Boolean = oldItem == newItem
}

class OrderAdapter() : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var listener: (Order,position: Int) -> Unit = { order: Order, position: Int -> }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun submitList(orders: List<Order>) {
        differ.submitList(orders)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    fun deleteItem(position: Int) {
        val currList = mutableListOf<Order>()
        currList.addAll(differ.currentList)
        currList.removeAt(position)
        notifyItemRemoved(position)
        submitList(currList)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.button.setOnClickListener {
            listener(differ.currentList[position], position)
        }
    }

    inner class OrderViewHolder(private val itemBinding: ItemOrderBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(order: Order) = with(itemBinding) {

            val textTitle = "Заказ №" + order.number
            textTitleOrder.text = textTitle
            val textDescription =
                order.quantity.toString() + " × " + order.size + " • " + order.name
            textDescriptionOrder.text = textDescription
            imagePreview.load(order.image) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
            val dateText = "Дата доставки: " + order.date2
            textDate.text = dateText
            val addressText = "Адрес доставки: \n" + order.address
            textAdres.text = addressText
            val amountText = "Сумма заказа: " + order.amount.toString() + " ₽"
            textAmount.text = amountText
            if (order.pickup) pickup.visibility = VISIBLE
        }

        val item = itemView
        val button = itemBinding.buttonDelete
    }
}
