package com.example.languageleap.ui.addText

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.languageleap.R
import androidx.lifecycle.lifecycleScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.languageleap.SharedDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.getValue


class AddTextFragment : Fragment() {


    private lateinit var titleEditText: EditText
    private lateinit var textEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var levelSpinner: Spinner
    private lateinit var imageButton: Button

    private lateinit var publicCheckbox: CheckBox
    private lateinit var saveButton: Button
    private var selectedImageUri: android.net.Uri? = null


    private val httpClient = OkHttpClient()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_text, container, false)
        titleEditText = view.findViewById(R.id.titleEditText)
        textEditText = view.findViewById(R.id.textEditText)
        languageSpinner = view.findViewById(R.id.languageSpinner)
        levelSpinner = view.findViewById(R.id.levelSpinner)
        imageButton = view.findViewById(R.id.imageButton)

        publicCheckbox = view.findViewById(R.id.publicCheckbox)
        saveButton = view.findViewById(R.id.saveButton)
        imageButton.setOnClickListener { selectImageFile() }

        saveButton.setOnClickListener { saveContent() }
        setupSpinners()
        return view
    }


    private fun selectImageFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }



    // Этот метод обрабатывает выбор файла после возвращения из системного Intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == android.app.Activity.RESULT_OK && data != null) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    selectedImageUri = data.data
                    Toast.makeText(context, "Изображение выбрано", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    private fun setupSpinners() {
        context?.let { ctx ->
            ArrayAdapter.createFromResource(
                ctx,
                R.array.language_options, // Замените на ваш массив языков
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                languageSpinner.adapter = adapter
            }
            ArrayAdapter.createFromResource(
                ctx,
                R.array.language_levels, // Замените на ваш массив уровней
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                levelSpinner.adapter = adapter
            }
        }
    }

    private fun saveContent() {


        val title = titleEditText.text.toString().trim()
        val textContent = textEditText.text.toString().trim()
        val language = languageSpinner.selectedItem.toString()
        val level = levelSpinner.selectedItem.toString()
        val isPublic = publicCheckbox.isChecked

        if (title.isEmpty() || textContent.isEmpty()) {
            Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }




        lifecycleScope.launch {
            try {
                // 1. Подготовка текстовых частей (обычные поля формы)
                val requestBodyBuilder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("text", textContent )
                    .addFormDataPart("language", language )
                    .addFormDataPart("level", level )
                    .addFormDataPart("isPublic", isPublic.toString() )

                // Добавьте остальные поля аналогично

                // 2. Подготовка файла изображения
                // Нам нужно получить реальный путь к файлу из URI для OkHttp
                selectedImageUri?.let { uri ->
                    val imagePath = getPathFromUri(uri)
                    if (imagePath != null) {
                        val imageFile = File(imagePath)
                        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaType())
                        requestBodyBuilder.addFormDataPart(
                            "image_file", // Имя поля, которое ожидает ваш сервер
                            imageFile.name,
                            imageRequestBody
                        )
                    } else {
                        Log.w("AddTextFragment", "Image file path was null, skipping image upload.")
                        // Тут можно показать Toast, если хотите уведомить пользователя, что файл изображения не будет отправлен
                    }
                }




                val multipartRequestBody = requestBodyBuilder.build()

                // 4. Выполнение запроса
                val response = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url(sharedViewModel.host+"/api/new_text")
                        .addHeader("Authorization", "Token ${sharedViewModel.getToken()}")
                        .post(multipartRequestBody)
                        .build()
                    httpClient.newCall(request).execute()
                }

                // ... (Обработка ответа как в предыдущем примере) ...
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Файлы успешно загружены", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Ошибка загрузки: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                    response.body?.close()
                }

            } catch (e: Exception) {
                // ... (Обработка ошибок сети/файлов) ...
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddTextFragment", "Upload error", e)
                }
            }
        }
    }
/*
    // Вспомогательная функция для получения реального пути файла из URI контента
    private fun getPathFromUri(uri: android.net.Uri): String? {
        // В реальном приложении этот метод должен быть более robustным
        // и обрабатывать разные типы URI (content://, file://)
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(projection[0])
            val filePath = it.getString(columnIndex)
            it.close()
            return filePath
        }
        return null
    }
*/

    private fun getPathFromUri(uri: Uri): String? {
        // Используем стандартный MediaStore общий для всех медиафайлов
        val projection = arrayOf("_data")
        context?.contentResolver?.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow("_data")
                return cursor.getString(columnIndex)
            }
        }
        // Если contentResolver не помог (например, это file:// URI),
        // или если файл находится в кэше приложения (ContentScheme.File)
        if (uri.scheme == "file") {
            return uri.path
        }

        // В некоторых случаях (например, на Android 10+ при выборе файла из "Files" (DocumentsProvider)),
        // этот простой метод может не сработать. Для максимальной совместимости
        // нужно использовать более сложные утилиты FileUtil, но начнем с этого.
        return null
    }


    companion object {
        private const val IMAGE_PICK_CODE = 1000

    }


}