package com.example.ui

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

sealed class CropDoctorState {
    object Idle : CropDoctorState()
    object Loading : CropDoctorState()
    data class Success(val result: String) : CropDoctorState()
    data class Error(val message: String) : CropDoctorState()
}

class MainViewModel : ViewModel() {
    private val _cropDoctorState = MutableStateFlow<CropDoctorState>(CropDoctorState.Idle)
    val cropDoctorState: StateFlow<CropDoctorState> = _cropDoctorState.asStateFlow()

    fun analyzeCropImage(bitmap: Bitmap) {
        _cropDoctorState.value = CropDoctorState.Loading
        viewModelScope.launch {
            try {
                val base64Image = bitmap.toBase64()
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = "You are an expert agricultural AI. Analyze this crop image. Identify the plant, any visible diseases, pests, or deficiencies. Provide a severity assessment and recommend specific treatments or actions for the farmer."),
                                Part(
                                    inlineData = InlineData(
                                        mimeType = "image/jpeg",
                                        data = base64Image
                                    )
                                )
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Could not analyze the image. Please try again."
                _cropDoctorState.value = CropDoctorState.Success(resultText)

            } catch (e: Exception) {
                _cropDoctorState.value = CropDoctorState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
