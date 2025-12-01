package com.example.languageleap.ui.text

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.ui.quiz.JsonResponseCurrentWords
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.getValue


class TextFragment : Fragment() {
    private lateinit var textView: TextView
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    private val httpClient = OkHttpClient()
    private val gson = Gson()
    private lateinit var view: View
    private var punctuationMarks = "!\"#\$%&'()*+,./:;<=>?@^_`{|}~"

    private lateinit var apiUrl :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_text, container, false)
        textView = view.findViewById(R.id.textView10)
        textView.setText(sharedViewModel.currentText.text.text)

        /*
        for (word in sharedViewModel.CurrentWords!!.all_words){
            highlightWord(word.word)
        }*/

        textView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val x = event.x.toFloat()
                val y = event.y.toFloat()
                val offset = textView.getOffsetForPosition(x, y)
                val text = textView.text.toString()

                val words = text.split(" ")
                var wordStart = 0
                for (word in words) {
                    val wordEnd = wordStart + word.length
                    if (offset in wordStart until wordEnd) {
                        saveWord(word)
                        break
                    }
                    wordStart = wordEnd + 1 // Учитываем пробел
                }
            }
            true
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (word in sharedViewModel.CurrentWords.all_words){
            highlightWord(word.word)
        }
    }

    private fun saveWord(word: String){
        highlightWord(word)
        var cleanWord = word.lowercase()
        cleanWord = cleanWord.filterNot { it in punctuationMarks.toSet() }


        highlightWord(cleanWord)
        apiUrl = sharedViewModel.host+"/translate_word/"+sharedViewModel.getLanguageCode()+"/"+cleanWord
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
            val response = gson.fromJson(jsonString, JsonResponseTranslateWord::class.java)
            Snackbar.make(view, "${response.word} - ${response.response.translation}", Snackbar.LENGTH_INDEFINITE).show()


        } catch (e: Exception) {
            Log.e("HomeFragment", "Ошибка парсинга JSON: ${e.message}", e)
            Toast.makeText(context, "Ошибка обработки данных: ${e.message} ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightWord(word: String) {
        val spannable = SpannableString(textView.text)
        var start = 0
        while (start < spannable.length) {
            start = spannable.indexOf(word, start)
            if (start == -1) break // Если вхождение не найдено, выходим из цикла

            spannable.setSpan(BackgroundColorSpan(Color.YELLOW), start, start + word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start += word.length // Увеличиваем индекс для поиска следующего вхождения
        }
        textView.text = spannable
    }



}


