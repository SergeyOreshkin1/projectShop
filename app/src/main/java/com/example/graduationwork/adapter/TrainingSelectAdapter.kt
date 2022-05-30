package com.example.graduationwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.data.entity.Model
import com.example.graduationwork.R
import com.example.graduationwork.databinding.CardItemTraining1Binding

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Model>() {

    override fun areItemsTheSame(oldItem: Model, newItem: Model):
            Boolean = oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: Model, newItem: Model):
            Boolean = oldItem == newItem
}

class TrainingSelectAdapter : RecyclerView.Adapter<TrainingSelectAdapter.ModelViewHolder>() {
    var listener: (Model) -> Unit = {}

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun submitList(models: List<Model>) {

        differ.submitList(models)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder =
        ModelViewHolder(
            CardItemTraining1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.itemView.setOnClickListener {
            listener(differ.currentList[position])
        }
    }

    class ModelViewHolder(private val itemBinding: CardItemTraining1Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(model: Model) = with(itemBinding) {


            title.text = model.title
            description.text = model.description
            time.text = model.time
            banner.load(model.image) {
                placeholder(R.drawable.placeholdertraining)

            }

        }
    }
}
