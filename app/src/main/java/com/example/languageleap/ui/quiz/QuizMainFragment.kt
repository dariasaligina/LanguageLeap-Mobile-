package com.example.languageleap.ui.quiz

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavAction
import androidx.navigation.fragment.findNavController
import com.example.languageleap.MainActivity
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.R

import com.example.languageleap.ui.home.TextCardAdapter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.DrbgParameters


class QuizMainFragment : Fragment() {
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    private val httpClient = OkHttpClient()
    private val gson = Gson()

    private val apiUrl = "http://192.168.0.34:8000/api/learn"


    private lateinit var container: ViewGroup
    private lateinit var currentWords: JsonResponseCurrentWords

    private lateinit var currentView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container1: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        try{
            container = container1!!
            currentView = inflater.inflate(R.layout.fragment_quiz_main, container, false)
            var button: Button = currentView.findViewById(R.id.button2)
            loadDataFromNetwork()
            button.setOnClickListener {
                showNextQuestion()
            }
       }
        catch (e: Exception){
            Log.e("BuildLayout", "${e.message}", e)
        }
        return currentView
    }




    private fun showNextQuestion(){
        sharedViewModel.NextWord+=1
        if (sharedViewModel.CurrentWords == null || sharedViewModel.CurrentWords!!.words.size <= sharedViewModel.NextWord){
            loadDataFromNetwork()
            sharedViewModel.NextWord = 0
            if (sharedViewModel.CurrentWords == null || sharedViewModel.CurrentWords!!.words.size <= sharedViewModel.NextWord)
                return
        }
        val word = sharedViewModel.CurrentWords!!.words[sharedViewModel.NextWord]

        if (word.knowledge == 1){
            try{
                findNavController().navigate(R.id.quizAudioFragment)
            } catch (e: Exception){
                Log.e("ChangeLayout", "${e.message}", e)
            }
        }
    }




    fun correct_answer(){
        Toast.makeText(context, "correct ", Toast.LENGTH_SHORT).show()

    }
    fun wrong_answer(){
        Toast.makeText(context, "wrong", Toast.LENGTH_SHORT).show()

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
                        parseData(jsonString)
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

    private fun parseData(jsonString: String) {
        try {
            val response = gson.fromJson(jsonString, JsonResponseCurrentWords::class.java)
            var text: TextView = currentView.findViewById(R.id.textView3)
            text.setText("Сегодня вам следует повторить ${response.words.size} слов.")
            currentWords=response
            sharedViewModel.CurrentWords = response


        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)
            Toast.makeText(context, "Ошибка обработки данных: ${e.message} ", Toast.LENGTH_SHORT).show()
        }
    }


}