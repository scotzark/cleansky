package com.kahana.cleansky.network

import android.location.Location
import com.kahana.cleansky.models.AirQualityApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AirQualityApi {
    @GET("/feed/geo:{latitude};{longitude}")
    suspend fun getAirQualityData(@Path("longitude")longitude: Double, @Path("latitude")latitude: Double): AirQualityApiResponse
}