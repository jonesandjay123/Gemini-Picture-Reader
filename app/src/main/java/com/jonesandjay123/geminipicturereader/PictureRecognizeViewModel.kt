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

    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
    )

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        safetySettings = safetySettings
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

    fun resetUiState() {
        _uiState.value = UiState.Initial
    }

    // 獲取提示詞
    fun getPrompt(language: String, category: String): String {
        return when (language) {
            "EN" -> when (category) {
                "Recognition" -> "Describe this image"
                "Motivational Story" -> "Generate a motivational story based on this image"
                "Joke" -> "Generate a joke based on this image"
                "Love Story" -> "Generate a love story based on this image"
                "Horror Story" -> "Generate a horror story based on this image"
                else -> "Describe this image"
            }
            "中文" -> when (category) {
                "識別" -> "請用繁體中文描述圖片中的內容"
                "激勵故事" -> "請根據這張圖片，發想對應的人、事、物，接著根據這些元素以繁體中文講一個激勵人心的故事"
                "笑話" -> "請根據這張圖片，發想對應的人、事、物，接著根據這些元素以繁體中文講一個笑話"
                "愛情故事" -> "請根據這張圖片，發想對應的人、事、物，接著根據這些元素以繁體中文講一個淒美的愛情故事"
                "恐怖故事" -> "請根據這張圖片，發想對應的人、事、物，接著根據這些元素以繁體中文講一個令人毛骨悚然的恐怖故事"
                else -> "請用繁體中文描述圖片中的內容"
            }
            else -> "Describe this image"
        }
    }

    // 獲取按鈕文字
    fun getButtonText(language: String, category: String): String {
        return when (language) {
            "EN" -> when (category) {
                "Recognition" -> "AI Recognition"
                "Motivational Story" -> "AI Tell a Motivational Story"
                "Joke" -> "AI Tell a Joke"
                "Love Story" -> "AI Tell a Love Story"
                "Horror Story" -> "AI Tell a Horror Story"
                else -> "AI Recognition"
            }
            "中文" -> when (category) {
                "識別" -> "AI識別"
                "激勵故事" -> "AI編一個激勵人心的故事"
                "笑話" -> "AI編一個笑話"
                "愛情故事" -> "AI編一個愛情故事"
                "恐怖故事" -> "AI編一個恐怖故事"
                else -> "AI識別"
            }
            else -> "AI Recognition"
        }
    }
}