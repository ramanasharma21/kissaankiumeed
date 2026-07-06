package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val current: CurrentWeather?,
    val daily: DailyWeather?
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "weather_code") val weatherCode: Int?
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>?
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double = 28.6139, // Default to New Delhi
        @Query("longitude") longitude: Double = 77.2090,
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("daily") daily: String = "precipitation_probability_max",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

object WeatherRetrofitClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val service: WeatherApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(WeatherApiService::class.java)
    }
}
