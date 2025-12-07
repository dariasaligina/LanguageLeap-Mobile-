package com.example.languageleap.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.languageleap.R
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.languageleap.SharedDataViewModel
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import kotlin.getValue


class RegisterFragment : Fragment() {

    private lateinit var view: View
    private lateinit var toLogin: TextView
    private val client = OkHttpClient()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = inflater.inflate(R.layout.fragment_register, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toLogin = view.findViewById(R.id.textView2)
        toLogin.setOnClickListener {
            findNavController().navigate(R.id.nav_login)
        }
        val buttonLogin: Button = view.findViewById(R.id.buttonRegister)
        buttonLogin.setOnClickListener {
            val email = view.findViewById<TextView>(R.id.editTextTextEmailAddress).text.toString().trim()
            val username = view.findViewById<TextView>(R.id.editTextText).text.toString().trim()
            val password1 = view.findViewById<TextView>(R.id.editTextTextPassword).text.toString()
            val password2 = view.findViewById<TextView>(R.id.editTextTextPassword2).text.toString()
            val language = view.findViewById<Spinner>(R.id.spinner).selectedItem.toString()
            if (password2!=password1){
                Toast.makeText(context, "Пароли должны совпадать", Toast.LENGTH_SHORT).show()
            }
            else if (email.isEmpty() or username.isEmpty() or password1.isEmpty() or language.isEmpty()){
                Toast.makeText(context, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            }
            else{


            val requestBody = RegisterRequest(username, email,password1,language)
            val json = gson.toJson(requestBody)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = RequestBody.create(mediaType, json)
            val request = Request.Builder()
                .url(sharedViewModel.host+"/api/register")
                .post(body)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Login", "Error: ${e.message}")
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Log.e("Login", "Error parsing response: ${e.message}")
                    }

                }
            })}

        }


    }


}