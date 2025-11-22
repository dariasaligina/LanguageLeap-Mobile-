package com.example.languageleap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.languageleap.ui.quiz.JsonResponseCurrentWords
import com.example.languageleap.ui.quiz.Word
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Callback



// 1. Определите класс данных (Data Class) для хранения ответа API
// Этот класс должен соответствовать структуре вашего JSON ответа от Django.
// Например, если ваш API возвращает токен и имя пользователя:
data class AuthResponse(
    val token: String,      // Токен для последующих запросов
    val username: String,   // Имя пользователя
    val userId: Int         // ID пользователя
)
data class LoginRequest(
    val username: String,
    val password: String
)

class SharedDataViewModel : ViewModel() {

    // 2. MutableLiveData для хранения состояния аутентификации (объект ответа)
    // Используем nullable тип (AuthResponse?), так как изначально данных нет
    private val _authData = MutableLiveData<AuthResponse?>()

    val authData: LiveData<AuthResponse?> = _authData
    private val client = OkHttpClient()
    private val gson = Gson()
    var CurrentWords: JsonResponseCurrentWords? = null
    var NextWord:Int = -1


    fun getToken():String{

        return _authData.value!!.token
    }



    // 4. Функция для обновления данных после успешной авторизации
    fun setAuthenticationData(data: AuthResponse) {
        _authData.value = data
    }

    // 5. Функция для проверки, вошел ли пользователь в систему
    fun isLoggedIn(): Boolean {
        return _authData.value != null && _authData.value?.token != null
    }

    // 6. Функция для выхода из системы (очистка данных)
    fun logout() {
        _authData.value = null
    }

    fun loginUser(username: String, password:String){
        val requestBody = LoginRequest(username, password)
        val json = gson.toJson(requestBody)
        // Create the media type and request body
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, json)
        val request = Request.Builder()
            .url("http://192.168.0.34:8000/api/login")  // Replace with your actual endpoint
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Error: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() // Read the response body
                        Log.d("Login", "Response: $responseBody") // Log response for debugging

                        responseBody?.let {
                            // Attempt to parse the response to AuthResponse
                            val authResponse = gson.fromJson(it, AuthResponse::class.java)
                            _authData.postValue(authResponse)

                        } ?: run {
                            Log.e("Login", "Response body is null")
                        }
                    } else {
                        Log.e("Login", "Error: ${response.message}")
                    }

                } catch (e: Exception) {
                    Log.e("Login", "Error parsing response: ${e.message}")
                }

            }
        })
    }
}




