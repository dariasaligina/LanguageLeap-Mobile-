package com.example.languageleap.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.databinding.ItemTextCardBinding
import com.squareup.picasso.Picasso
import kotlin.getValue

class TextCardAdapter(private val textList: List<TextCardItem>, private val sharedViewModel: SharedDataViewModel) :
    RecyclerView.Adapter<TextCardAdapter.TextCardViewHolder>() {


    inner class TextCardViewHolder(private val binding: ItemTextCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(textItem: TextCardItem) {
            binding.textViewTitle.text = textItem.name
            binding.textViewLevel.text = textItem.language_level
            binding.textViewLikes.text = textItem.likes.toString()

            // Construct the full image URL. Adjust the base URL if your server is different.
            val baseUrl = sharedViewModel.host
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