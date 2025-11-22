package com.example.languageleap.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.databinding.FragmentHomeBinding
import com.example.languageleap.databinding.FragmentProfileBinding
import com.example.languageleap.ui.home.HomeViewModel
import com.example.languageleap.ui.home.JsonResponseCatalog
import com.example.languageleap.ui.home.TextCardAdapter
import com.example.languageleap.ui.home.TextCardItem
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.getValue



class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var textAdapter: TextCardAdapter
    private val sharedViewModel: SharedDataViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val httpClient = OkHttpClient()
    private val gson = Gson()

    private val apiUrl = "http://192.168.0.34:8000/api/profile"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupRecyclerView()
        loadDataFromNetwork()


        return root
    }

    private fun setupRecyclerView() {
        // ... (код setupRecyclerView) ...
    }

    private fun loadDataFromNetwork() {
        // Запускаем корутину в жизненном цикле фрагмента
        lifecycleScope.launch(Dispatchers.IO) {
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
                        parseAndDisplayData(jsonString)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Ошибка сетевого запроса: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun parseAndDisplayData(jsonString: String) {
        try {
            val response = gson.fromJson(jsonString, JsonResponseProfile::class.java)

            // Обновите RecyclerView для каждой категории
            setupRecyclerView(binding.recyclerViewMyTexts, response.my_texts, "Мои тексты")
            setupRecyclerView(binding.recyclerViewCompletedTexts, response.completed_texts, "Завершенные тексты")
            setupRecyclerView(binding.recyclerViewCurrentTexts, response.current_texts, "Текущие тексты")
            setupRecyclerView(binding.recyclerViewFutureTexts, response.future_texts, "Будущие тексты")



        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)
            Toast.makeText(context, "Ошибка обработки данных: ${e.message} ", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupRecyclerView(recyclerView: RecyclerView, texts: List<TextCardItem>, category: String) {
        val adapter = TextCardAdapter(texts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}