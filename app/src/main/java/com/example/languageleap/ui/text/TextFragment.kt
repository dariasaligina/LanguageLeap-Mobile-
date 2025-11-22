package com.example.languageleap.ui.text

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.languageleap.R
import com.google.android.material.snackbar.Snackbar


class TextFragment : Fragment() {
    private lateinit var textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_text, container, false)
        textView = view.findViewById(R.id.textView10)
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
                        highlightWord(word)
                        break
                    }
                    wordStart = wordEnd + 1 // Учитываем пробел
                }
            }
            true
        }
        highlightWord("people")
        highlightWord("meal")
        highlightWord("event")
        highlightWord("ship")
        Snackbar.make(view, "ship - корабль", Snackbar.LENGTH_INDEFINITE).show()

        return view
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