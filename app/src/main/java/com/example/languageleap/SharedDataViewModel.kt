package com.example.languageleap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.languageleap.ui.quiz.JsonResponseCurrentWords
import com.example.languageleap.ui.quiz.Word
import com.example.languageleap.ui.text.JsonTextResponce
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Callback
import org.intellij.lang.annotations.Language



data class AuthResponse(
    val token: String,
    val username: String,
    val userId: Int,
    val languageCode: String
)
data class LoginRequest(
    val username: String,
    val password: String
)

class SharedDataViewModel : ViewModel() {


    private val _authData = MutableLiveData<AuthResponse?>()
    val host:String = "http://192.168.0.34:8000"

    val authData: LiveData<AuthResponse?> = _authData
    private val client = OkHttpClient()
    private val gson = Gson()
    var CurrentWords: JsonResponseCurrentWords = JsonResponseCurrentWords(emptyArray(),emptyArray())
    var NextWord:Int = -1

    lateinit var currentText: JsonTextResponce


    fun getToken():String{

        return _authData.value!!.token
    }

    fun getLanguageCode():String{
        return _authData.value!!.languageCode
    }



    fun setAuthenticationData(data: AuthResponse) {
        _authData.value = data
    }


    fun isLoggedIn(): Boolean {
        return _authData.value != null && _authData.value?.token != null
    }

    fun logout() {
        _authData.value = null
    }

    fun loginUser(username: String, password:String){
        val requestBody = LoginRequest(username, password)
        val json = gson.toJson(requestBody)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, json)
        val request = Request.Builder()
            .url(host+"/api/login")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Error: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("Login", "Response: $responseBody")

                        responseBody?.let {
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




