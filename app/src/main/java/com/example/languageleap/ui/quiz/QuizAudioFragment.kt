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
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.getValue


class QuizAudioFragment : Fragment() {
    private lateinit var currentView: View
    private lateinit var sharedViewModel: SharedDataViewModel
    lateinit var word:Word
    private val httpClient = OkHttpClient()
    lateinit var all_words: Array<Word>
    companion object {
        fun newInstance(): QuizAudioFragment {
            return QuizAudioFragment()
        }
    }

    fun correct_answer(){

        Snackbar.make(currentView, "Правильно!! ${word.word} - ${word.translation}", Snackbar.LENGTH_INDEFINITE).show()
        updateWord(1)
        showNextQuestion()

    }
    fun wrong_answer(){

        Snackbar.make(currentView, "Неправильно :( ${word.word} - ${word.translation}", Snackbar.LENGTH_INDEFINITE).show()
        updateWord(0)
        showNextQuestion()

    }


    private fun updateWord(isCorrect: Int){

        val apiUrl = sharedViewModel.host+"/saved_word_update/"+word.saved_word_id.toString()+"/"+isCorrect.toString()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Token ${sharedViewModel.getToken()}")
                    .build()

                // Выполняем синхронный запрос в фоновом потоке (Dispatchers.IO)
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw _root_ide_package_.okio.IOException("Unexpected code $response") as Throwable


                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Ошибка сетевого запроса: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedViewModel1: SharedDataViewModel by activityViewModels()
        sharedViewModel = sharedViewModel1

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_quiz_audio, container, false)

        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.CurrentWords?.let {
            word = it.words[sharedViewModel.NextWord]
            all_words = it.all_words
        }



        all_words.shuffle()
        var arr = arrayOf(all_words[0], all_words[1], all_words[2], word)
        arr.shuffle()
        val button: ImageButton = currentView.findViewById(R.id.imageButton2)
        button.setOnClickListener {
            val url = sharedViewModel.host+word.audio // your URL here
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
            if (word.knowledge == 1)
                buttons[i].setText(arr[i].word)
            else
                buttons[i].setText(arr[i].translation)
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

    private fun showNextQuestion(){
        sharedViewModel.NextWord+=1
        findNavController().popBackStack()
        if (sharedViewModel.CurrentWords == null || sharedViewModel.CurrentWords!!.words.size <= sharedViewModel.NextWord){


            findNavController().navigate(R.id.nav_learn)
        }
        val word = sharedViewModel.CurrentWords!!.words[sharedViewModel.NextWord]
        if (word.knowledge == 1 || word.knowledge==3){
            try{
                findNavController().navigate(R.id.quizAudioFragment)
            } catch (e: Exception){
                Log.e("ChangeLayout", "${e.message}", e)
            }
        }
        else if (word.knowledge== 2 || word.knowledge == 4){
            try{
                findNavController().navigate(R.id.quizTextFragment)
            } catch (e: Exception){
                Log.e("ChangeLayout", "${e.message}", e)
            }
        }
        else{
            try{
                findNavController().navigate(R.id.quizCardFragment)
            } catch (e: Exception){
                Log.e("ChangeLayout", "${e.message}", e)
            }
        }
    }


}