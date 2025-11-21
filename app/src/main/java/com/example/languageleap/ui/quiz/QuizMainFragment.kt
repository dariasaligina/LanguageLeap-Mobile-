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


class QuizMainFragment : Fragment() {
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    private val httpClient = OkHttpClient()
    private val gson = Gson()

    private val apiUrl = "http://192.168.0.34:8000/api/learn"


    private lateinit var container: ViewGroup
    private lateinit var currentWords: JsonResponseCurrentWords
    private val mainLayout = R.layout.fragment_quiz_main
    private val audioLayout = R.layout.fragment_quiz_audio
    private val textLayout = R.layout.fragment_quiz_text
    private val cardLayout = R.layout.fragment_quiz_fleshcard
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
        currentView= inflater.inflate(mainLayout, container, false)
        //audioLayout = inflater.inflate(R.layout.fragment_quiz_audio, container, false)
        //textLayout = inflater.inflate(R.layout.fragment_quiz_text, container, false)
        //cardLayout = inflater.inflate(R.layout.fragment_quiz_fleshcard, container, false)

        var text: TextView = currentView.findViewById( R.id.textView3)
        var button: Button = currentView.findViewById(R.id.button2)
        loadDataFromNetwork()
        button.setOnClickListener {
            runQuiz();
        }
       }
        catch (e: Exception){
            Log.e("BuildLayout", "${e.message}", e)
        }
        return currentView
    }


    private fun runQuiz(){
        var words= currentWords.words
        for (word in words)
            showQuestion(word)


    }

    private fun showQuestion(word: Word){
        var all_words = currentWords.all_words
        all_words.shuffle()
        var arr = arrayOf(all_words[0], all_words[1], all_words[2], word)
        arr.shuffle()
        if (word.knowledge == 1){
            changeLayout(audioLayout)

            val button: ImageButton = currentView.findViewById(R.id.imageButton2)
            button.setOnClickListener {
                val url = "http://192.168.0.34:8000"+word.audio // your URL here
                val mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(url)
                    prepare() // might take long! (for buffering, etc)
                    start()
                }
            }
            val buttons = arrayOf<Button>(currentView.findViewById(R.id.button6),
                currentView.findViewById(R.id.button7),
                currentView.findViewById(R.id.button8),
                currentView.findViewById(R.id.button9))
            for (i in 0..3){
                buttons[i].setText(arr[i].word)
                if (arr[i] == word)
                    buttons[i].setOnClickListener{
                        correct_answer()
                    }
                else
                    buttons[i].setOnClickListener{
                        wrong_answer()
                    }
            }
        }
    }

    private fun changeLayout(newLayoutResource: Int) {
        try{
            val newLayout = LayoutInflater.from(context).inflate(newLayoutResource, container, false)

            val parent = currentView.parent as ViewGroup
            parent.removeView(currentView)
            parent.addView(newLayout)

            currentView = newLayout
        }
        catch (e: Exception){
            Log.e("ChangeLayout", "${e.message}", e)
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


        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)
            Toast.makeText(context, "Ошибка обработки данных: ${e.message} ", Toast.LENGTH_SHORT).show()
        }
    }


}