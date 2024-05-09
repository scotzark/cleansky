package com.kahana.cleansky.network

class AirQualityApiImp(private val apiService: AirQualityApi) {
    suspend fun getAirQualityData() = apiService.getAirQualityData()
}