package com.kahana.cleansky.network

import android.location.Location

class AirQualityApiImp(private val apiService: AirQualityApi) {
    suspend fun getAirQualityData(location: Location) = apiService.getAirQualityData(location.longitude, location.latitude)
}