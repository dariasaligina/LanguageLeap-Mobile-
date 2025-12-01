package com.example.languageleap.ui.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.getValue


class QuizFleshcardFragment : Fragment() {

    private lateinit var currentView: View
    private lateinit var sharedViewModel: SharedDataViewModel
    lateinit var word:Word
    lateinit var all_words: Array<Word>
    private val httpClient = OkHttpClient()

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
        currentView= inflater.inflate(R.layout.fragment_quiz_fleshcard, container, false)
        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.CurrentWords?.let {
            word = it.words[sharedViewModel.NextWord]
            all_words = it.all_words
        }



        val text: TextView = currentView.findViewById(R.id.textView7)
        if (word.knowledge == 5){
            text.setText(word.word)
        }
        else{
            text.setText(word.translation)
        }

        val ans: TextView = currentView.findViewById(R.id.textView8)
        if (word.knowledge == 6){
            ans.setText(word.word)
        }
        else{
            ans.setText(word.translation)
        }
        val button_correct: Button = currentView.findViewById(R.id.button5)
        val button_wrong: Button = currentView.findViewById(R.id.button10)
        text.setOnClickListener {
            val div:View =currentView.findViewById(R.id.divider)
            div.visibility = View.VISIBLE
            ans.visibility = View.VISIBLE
            button_wrong.visibility = View.VISIBLE
            button_correct.visibility = View.VISIBLE

        }
        button_correct.setOnClickListener {
            correct_answer()
        }
        button_wrong.setOnClickListener {
            wrong_answer()
        }



    }

    fun correct_answer(){

        updateWord(1)
        showNextQuestion()

    }
    fun wrong_answer(){

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


}