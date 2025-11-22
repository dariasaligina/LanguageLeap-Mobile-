package com.example.languageleap.ui.profile

import com.example.languageleap.ui.home.TextCardItem

data class JsonResponseProfile(
    val my_texts: List<TextCardItem>,
    val completed_texts: List<TextCardItem>,
    val current_texts: List<TextCardItem>,
    val future_texts: List<TextCardItem>,
)