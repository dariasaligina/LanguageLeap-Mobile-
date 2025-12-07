package com.example.languageleap.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.languageleap.R
import com.example.languageleap.SharedDataViewModel
import android.widget.Toast
import android.widget.TextView
import androidx.navigation.fragment.findNavController


class FragmentLogin : Fragment() {


    private val sharedViewModel: SharedDataViewModel by activityViewModels()

    // Переменные для доступа к элементам UI (альтернатива View Binding)
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var toRegister: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // 2. Инициализируем элементы UI после инфлейта
        editTextUsername = view.findViewById(R.id.editTextText) // ID из вашего XML
        editTextPassword = view.findViewById(R.id.editTextTextPassword) // ID из вашего XML
        buttonLogin = view.findViewById(R.id.button) // ID из вашего XML
        toRegister = view.findViewById(R.id.textView2)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Устанавливаем слушатель нажатия на кнопку
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Вызываем функцию логина во ViewModel
                // ВАЖНО: Мы переместим логику OkHttp в ViewModel
                sharedViewModel.loginUser(username, password)

                sharedViewModel.authData.observe(viewLifecycleOwner) { authResponse ->
                    authResponse?.let {
                        // Show token if authentication is successful
                        Toast.makeText(context, "Аутентификация прошла успешно", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(context, "Ошибка аутентификации", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(context, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }

        toRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_registration)
        }

    }


}