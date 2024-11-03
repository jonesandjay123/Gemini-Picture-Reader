// PictureRecognizeViewModel.kt
package com.jonesandjay123.geminipicturereader

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PictureRecognizeViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE)
    private val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE)
    private val sexSpeechSafety =SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)
    private val dangerousSpeechSafety =SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety, sexSpeechSafety, dangerousSpeechSafety)
    )

    fun recognizeImage(bitmap: Bitmap, prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                } ?: run {
                    _uiState.value = UiState.Error("No text returned from the model.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An unknown error occurred.")
            }
        }
    }

    // 在 PictureRecognizeViewModel 類別中添加以下方法
    fun resetUiState() {
        _uiState.value = UiState.Initial
    }
}