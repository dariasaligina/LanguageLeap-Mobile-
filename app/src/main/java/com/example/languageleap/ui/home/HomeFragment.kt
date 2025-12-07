package com.example.languageleap.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.languageleap.R
import com.example.languageleap.databinding.FragmentHomeBinding
import com.google.gson.Gson
import androidx.recyclerview.widget.RecyclerView
import com.example.languageleap.SharedDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.getValue

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var textAdapter: TextCardAdapter
    private val binding get() = _binding!!
    private val httpClient = OkHttpClient()
    private val gson = Gson()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    private lateinit var apiUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!sharedViewModel.isLoggedIn()){
            findNavController().navigate(R.id.nav_login)
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        apiUrl =sharedViewModel.host + "/json/catalog"
        setupRecyclerView()
        loadDataFromNetwork()
        return root
    }

    private fun setupRecyclerView() {

    }

    private fun loadDataFromNetwork() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(apiUrl)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw _root_ide_package_.okio.IOException("Unexpected code $response") as Throwable

                    val jsonString = response.body?.string() ?: ""

                    withContext(Dispatchers.Main) {
                        parseAndDisplayData(jsonString)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Ошибка сетевого запроса: ${e.message}", e)

            }
        }
    }

    private fun parseAndDisplayData(jsonString: String) {
        try {
            val response = gson.fromJson(jsonString, JsonResponseCatalog::class.java)
            val texts = response.texts

            textAdapter = TextCardAdapter(texts, sharedViewModel, viewLifecycleOwner.lifecycleScope, this)
            binding.recyclerViewTexts.adapter = textAdapter

            Log.d("HomeFragment", "Данные успешно отображены. Количество: ${texts.size}")

        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}