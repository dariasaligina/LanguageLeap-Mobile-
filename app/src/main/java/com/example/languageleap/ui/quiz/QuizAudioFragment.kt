package com.example.languageleap.ui.quiz

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import com.example.languageleap.databinding.ActivityMainBinding
import java.io.File
import kotlin.getValue


class QuizAudioFragment : Fragment() {
    private lateinit var currentView: View
    private lateinit var sharedViewModel: SharedDataViewModel
    lateinit var word:Word
    lateinit var all_words: Array<Word>
    companion object {
        fun newInstance(): QuizAudioFragment {
            return QuizAudioFragment()
        }
    }

    fun correct_answer(){
        Toast.makeText(context, "correct ", Toast.LENGTH_SHORT).show()

    }
    fun wrong_answer(){
        Toast.makeText(context, "wrong", Toast.LENGTH_SHORT).show()

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