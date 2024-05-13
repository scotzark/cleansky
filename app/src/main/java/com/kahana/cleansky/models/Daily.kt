package com.kahana.cleansky.models

import androidx.annotation.Keep

@Keep
data class Daily(val o3: List<DailyData>,
                 val pm10: List<DailyData>,
                 val pm25: List<DailyData>)
