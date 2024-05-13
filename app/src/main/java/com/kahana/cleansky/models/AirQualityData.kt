package com.kahana.cleansky.models

import androidx.annotation.Keep

@Keep
data class AirQualityData(val forecast: Forecast, val city: City, val attributions: List<Attribution>)
