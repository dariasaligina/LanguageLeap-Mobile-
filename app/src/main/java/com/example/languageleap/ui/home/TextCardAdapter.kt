package com.example.languageleap.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.databinding.ItemTextCardBinding
import com.example.languageleap.ui.quiz.JsonResponseCurrentWords
import com.example.languageleap.ui.text.JsonResponseTranslateWord
import com.example.languageleap.ui.text.JsonTextResponce
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.getValue


class TextCardAdapter(private val textList: List<TextCardItem>, private val sharedViewModel: SharedDataViewModel, private val scope: CoroutineScope,  private val fragment: Fragment) :
    RecyclerView.Adapter<TextCardAdapter.TextCardViewHolder>() {
    private val httpClient = OkHttpClient()
    private val gson = Gson()


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

                loadWordsFromNetwork()

                val apiUrl: String = sharedViewModel.host+"/api/text/${textItem.id}/"
                scope.launch(Dispatchers.IO) {
                    try {
                        val request = Request.Builder()
                            .url(apiUrl)
                            .addHeader("Authorization", "Token ${sharedViewModel.getToken()}")
                            .build()

                        // Выполняем синхронный запрос в фоновом потоке (Dispatchers.IO)
                        httpClient.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw _root_ide_package_.okio.IOException("Unexpected code $response") as Throwable

                            val jsonString = response.body?.string() ?: ""

                            // Переключаемся обратно в главный поток для обновления UI
                            withContext(Dispatchers.Main) {
                                parseTextData(jsonString)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Ошибка сетевого запроса: ${e.message}", e)

                    }
                }





            }
        }
    }

    private fun loadWordsFromNetwork() {
        // Запускаем корутину в жизненном цикле фрагмента
        scope.launch(Dispatchers.IO) {
            val apiUrl=sharedViewModel.host + "/api/learn"
            try {
                val request = Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Token ${sharedViewModel.getToken()}")
                    .build()

                // Выполняем синхронный запрос в фоновом потоке (Dispatchers.IO)
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw _root_ide_package_.okio.IOException("Unexpected code $response") as Throwable

                    val jsonString = response.body?.string() ?: ""

                    // Переключаемся обратно в главный поток для обновления UI
                    withContext(Dispatchers.Main) {
                        parseWordData(jsonString)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Ошибка сетевого запроса: ${e.message}", e)

            }
        }
    }

    private fun parseWordData(jsonString: String) {
        try {
            val response = gson.fromJson(jsonString, JsonResponseCurrentWords::class.java)

            sharedViewModel.CurrentWords = response


        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)

        }
    }




    private fun parseTextData(jsonString: String) {
        try {
            val response = gson.fromJson(jsonString, JsonTextResponce::class.java)
            sharedViewModel.currentText = response
            fragment.findNavController().navigate(R.id.textFragment)



        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)

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