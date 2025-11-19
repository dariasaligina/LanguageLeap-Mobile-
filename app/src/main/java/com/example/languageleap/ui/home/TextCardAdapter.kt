package com.example.languageleap.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.languageleap.databinding.ItemTextCardBinding
import com.squareup.picasso.Picasso

class TextCardAdapter(private val textList: List<TextItem>) :
    RecyclerView.Adapter<TextCardAdapter.TextCardViewHolder>() {

    inner class TextCardViewHolder(private val binding: ItemTextCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(textItem: TextItem) {
            binding.textViewTitle.text = textItem.name
            binding.textViewLevel.text = textItem.language_level
            binding.textViewLikes.text = textItem.likes.toString()

            // Construct the full image URL. Adjust the base URL if your server is different.
            val baseUrl = "http://192.168.0.34:8000"
            val imageUrl = baseUrl + textItem.image

            // Load image using Picasso
            Picasso.get().load(imageUrl).into(binding.imageViewTextImage)

            // Handle item clicks (optional)
            itemView.setOnClickListener {
                // Handle click event, e.g., open the full text activity
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextCardViewHolder {
        val binding = ItemTextCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TextCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TextCardViewHolder, position: Int) {
        holder.bind(textList[position])
    }

    override fun getItemCount(): Int = textList.size
}