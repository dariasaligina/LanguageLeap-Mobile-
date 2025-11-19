package com.example.languageleap.ui.home

data class TextItem(
    val id: Int,
    val name: String,
    val language_id: Int,
    val language_level: String,
    val image: String,
    val likes: Int
)

data class JsonResponse(
    val texts: List<TextItem>
)