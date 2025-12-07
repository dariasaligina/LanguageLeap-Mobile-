package com.example.languageleap.ui.register

data class RegisterRequest(
    val username:String,
    val email:String,
    val password:String,
    val language:String,
)
