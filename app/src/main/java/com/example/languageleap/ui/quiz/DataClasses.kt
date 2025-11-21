package com.example.languageleap.ui.quiz



data class Word(
    val id:Int,
    val saved_word_id:Int,
    val word: String,
    val translation: String,
    val audio: String,
    val knowledge: Int,
)

data class JsonResponseCurrentWords(
    val words: Array<Word>,
    val all_words: Array<Word>,
)