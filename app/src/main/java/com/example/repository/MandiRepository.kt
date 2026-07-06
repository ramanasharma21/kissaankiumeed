package com.example.repository

import com.example.api.AgmarknetApiService
import com.example.api.MandiRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class MandiRepository(
    private val apiService: AgmarknetApiService,
    private val apiKey: String
) {
    suspend fun getMandiPrices(state: String? = null, commodity: String? = null, limit: Int = 100): Result<List<MandiRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMandiPrices(
                    apiKey = apiKey,
                    state = state,
                    commodity = commodity,
                    limit = limit
                )
                
                val records = response.records
                if (records != null) {
                    Result.Success(records)
                } else {
                    Result.Error(Exception("No data found"))
                }
            } catch (e: Exception) {
                // Return encapsulated error for robust UI handling
                Result.Error(e)
            }
        }
    }
}
