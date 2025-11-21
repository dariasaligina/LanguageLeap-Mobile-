package com.example.languageleap.ui.home

data class TextCardItem(
    val id: Int,
    val name: String,
    val language_id: Int,
    val language_level: String,
    val image: String,
    val likes: Int
)

data class JsonResponseCatalog(
    val texts: List<TextCardItem>
)