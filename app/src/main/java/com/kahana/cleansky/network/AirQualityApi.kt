package com.kahana.cleansky.network

import com.kahana.cleansky.models.AirQualityApiResponse
import retrofit2.http.GET

interface AirQualityApi {
    @GET("/feed/geo:27.099291;-82.431480")
    suspend fun getAirQualityData(): AirQualityApiResponse
}