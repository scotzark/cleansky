package com.kahana.cleansky.models

import androidx.annotation.Keep

@Keep
class AirQualityApiResponse(val status: String, val data: AirQualityData)