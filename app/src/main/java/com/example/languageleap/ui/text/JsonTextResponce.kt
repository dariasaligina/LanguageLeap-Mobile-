package com.example.languageleap.ui.text

data class textResponce(
    val id:Int,
    val name: String,
    val text: String,
    val audio: String
)

data class JsonTextResponce(
    val text: textResponce,
    val text_status: Int
)
