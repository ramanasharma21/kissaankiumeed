package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)

class MandiViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val agmarknetApiService = com.example.api.AgmarknetRetrofitClient.createService(application)
    private val mandiRepository = com.example.repository.MandiRepository(
        apiService = agmarknetApiService,
        apiKey = BuildConfig.DATA_GOV_IN_API_KEY
    )

    private val systemInstructionText = """
        You are an AI Agricultural Market Assistant for KISSAAN AI.
        Your job is to provide accurate and up-to-date mandi prices for agricultural products across India.
        
        When a user asks for market prices:
        1. Detect the user's current location automatically (or ask for it if unavailable).
        2. Find the nearest APMC mandis.
        3. Show today's prices for the requested crop.
        4. Display:
           • Mandi Name
           • District
           • State
           • Crop Name
           • Variety
           • Minimum Price (₹/Quintal)
           • Maximum Price (₹/Quintal)
           • Modal Price (₹/Quintal)
           • Last Updated Time
        5. Compare prices from the five nearest mandis.
        6. Highlight the mandi offering the highest selling price.
        7. Suggest where the farmer should sell to maximize profit.
        8. If today's data is unavailable, show the latest available price and mention the date.
        9. Respond in a clear table format.
        10. Support English, Hindi, and regional Indian languages.
        
        Always provide reliable and farmer-friendly responses.
    """.trimIndent()

    init {
        // Initial welcome message
        _messages.value = listOf(
            ChatMessage(
                text = "Hello! I am your KISSAAN AI Market Assistant. Ask me about the latest mandi prices for any crop, and please mention your location if you haven't set it yet.",
                isUser = false
            )
        )
    }

    fun sendMessage(userText: String) {
        val userMessage = ChatMessage(text = userText, isUser = true)
        val loadingMessage = ChatMessage(text = "Thinking...", isUser = false, isLoading = true)
        
        _messages.value = _messages.value + userMessage + loadingMessage

        viewModelScope.launch {
            try {
                // Construct chat history for Gemini
                val contents = _messages.value
                    .filter { !it.isLoading && it.id != userMessage.id } // exclude loading and the current user message (will add below)
                    .dropWhile { !it.isUser } // Gemini requires conversation to start with "user"
                    .map { msg ->
                        Content(
                            role = if (msg.isUser) "user" else "model",
                            parts = listOf(Part(text = msg.text))
                        )
                    }.toMutableList()
                
                // Add the new user message
                contents.add(Content(role = "user", parts = listOf(Part(text = userText))))

                // Fetch real mandi data to feed to Gemini
                var marketContext = ""
                try {
                    val result = mandiRepository.getMandiPrices(limit = 20)
                    if (result is com.example.repository.Result.Success) {
                        val recordsStr = result.data.take(15).joinToString("\n") { 
                            "${it.market} (${it.state}, ${it.district}) - ${it.commodity} (${it.variety}): Min ₹${it.minPrice}, Max ₹${it.maxPrice}, Modal ₹${it.modalPrice} on ${it.arrivalDate}"
                        }
                        marketContext = "\n\nRecent Market Data from Agmarknet API:\n$recordsStr"
                    }
                } catch (e: Exception) {
                    marketContext = "\n\n(Could not fetch latest market data: ${e.message})"
                }

                val request = GenerateContentRequest(
                    contents = contents,
                    systemInstruction = Content(role = "system", parts = listOf(Part(text = systemInstructionText + marketContext)))
                )

                val response = RetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Sorry, I couldn't fetch the market prices at the moment. Please try again."

                // Remove loading message and add actual response
                _messages.value = _messages.value.filter { !it.isLoading } + ChatMessage(text = resultText, isUser = false)
            } catch (e: Exception) {
                _messages.value = _messages.value.filter { !it.isLoading } + ChatMessage(text = "An error occurred: ${e.message}", isUser = false)
            }
        }
    }
}
