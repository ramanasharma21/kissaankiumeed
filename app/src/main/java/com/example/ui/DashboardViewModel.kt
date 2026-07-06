package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WaterDrop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.WeatherRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val temperature: String = "--°C",
    val rainProbability: String = "--%",
    val condition: String = "Loading...",
    val icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.WbSunny
)

class DashboardViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherUiState(isLoading = true))
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val response = WeatherRetrofitClient.service.getWeather()
                val temp = response.current?.temperature2m?.let { "${it.toInt()}°C" } ?: "--°C"
                val rainProb = response.daily?.precipitationProbabilityMax?.firstOrNull()?.let { "$it%" } ?: "--%"
                
                // Map WMO weather code
                val code = response.current?.weatherCode ?: 0
                val (condition, icon) = mapWeatherCode(code)

                _weatherState.value = WeatherUiState(
                    isLoading = false,
                    temperature = temp,
                    rainProbability = rainProb,
                    condition = condition,
                    icon = icon
                )
            } catch (e: Exception) {
                _weatherState.value = _weatherState.value.copy(
                    isLoading = false,
                    condition = "Update failed"
                )
            }
        }
    }

    private fun mapWeatherCode(code: Int): Pair<String, androidx.compose.ui.graphics.vector.ImageVector> {
        return when (code) {
            0 -> "Clear sky" to Icons.Default.WbSunny
            1, 2, 3 -> "Partly cloudy" to Icons.Default.Cloud
            45, 48 -> "Fog" to Icons.Default.Cloud
            51, 53, 55, 56, 57 -> "Drizzle" to Icons.Default.Grain
            61, 63, 65, 66, 67 -> "Rain" to Icons.Default.WaterDrop
            71, 73, 75, 77 -> "Snow" to Icons.Default.AcUnit
            80, 81, 82 -> "Rain showers" to Icons.Default.WaterDrop
            85, 86 -> "Snow showers" to Icons.Default.AcUnit
            95, 96, 99 -> "Thunderstorm" to Icons.Default.FlashOn
            else -> "Unknown" to Icons.Default.WbSunny
        }
    }
}
