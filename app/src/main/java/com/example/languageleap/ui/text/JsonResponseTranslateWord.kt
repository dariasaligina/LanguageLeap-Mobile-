package com.example.languageleap.ui.text


data class TranslationResponse(
    val translation:String,
    val meaning:String
)


data class JsonResponseTranslateWord(
    val word:String,
    val response: TranslationResponse
)



