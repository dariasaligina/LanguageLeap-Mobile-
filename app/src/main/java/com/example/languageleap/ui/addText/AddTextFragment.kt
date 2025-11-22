package com.example.languageleap.ui.addText

import android.content.Intent
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



class AddTextFragment : Fragment() {


    private lateinit var titleEditText: EditText
    private lateinit var textEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var levelSpinner: Spinner
    private lateinit var imageButton: Button
    private lateinit var audioButton: Button
    private lateinit var publicCheckbox: CheckBox
    private lateinit var saveButton: Button
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
        audioButton = view.findViewById(R.id.audioButton)
        publicCheckbox = view.findViewById(R.id.publicCheckbox)
        saveButton = view.findViewById(R.id.saveButton)
        imageButton.setOnClickListener { selectImageFile() }
        audioButton.setOnClickListener { selectAudioFile() }
        saveButton.setOnClickListener { saveContent() }
        return view
    }
    private fun selectImageFile() {
        // Логика выбора изображения (используйте Intent для выбора файла)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    private fun selectAudioFile() {
        // Логика выбора аудио файла
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, AUDIO_PICK_CODE)
    }
    private fun saveContent() {
        // Логика сохранения контента
        Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
    }
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val AUDIO_PICK_CODE = 1001
    }


}