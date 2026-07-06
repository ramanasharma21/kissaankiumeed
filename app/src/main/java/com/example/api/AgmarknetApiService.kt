package com.example.api

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class MandiPriceResponse(
    val records: List<MandiRecord>?,
    val total: Int?
)

@JsonClass(generateAdapter = true)
data class MandiRecord(
    val state: String?,
    val district: String?,
    val market: String?,
    val commodity: String?,
    val variety: String?,
    val grade: String?,
    @Json(name = "arrival_date") val arrivalDate: String?,
    @Json(name = "min_price") val minPrice: String?,
    @Json(name = "max_price") val maxPrice: String?,
    @Json(name = "modal_price") val modalPrice: String?
)

interface AgmarknetApiService {
    @GET("resource/9ef84268-d588-465a-a308-a864a43d0070")
    suspend fun getMandiPrices(
        @Query("api-key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 100,
        @Query("filters[state]") state: String? = null,
        @Query("filters[commodity]") commodity: String? = null
    ): MandiPriceResponse
}

object AgmarknetRetrofitClient {
    private const val BASE_URL = "https://api.data.gov.in/"
    
    fun createService(context: Context): AgmarknetApiService {
        // 10 MB cache
        val cacheSize = (10 * 1024 * 1024).toLong() 
        val cache = Cache(File(context.cacheDir, "mandi_cache"), cacheSize)

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                // Custom caching interceptor
                var request = chain.request()
                // Fetch from cache if network is unavailable, otherwise use network
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 60 * 5) // 5 minutes cache
                    .build()
                val response = chain.proceed(request)
                response
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            
        return retrofit.create(AgmarknetApiService::class.java)
    }
}
