package com.kahana.cleansky.models

data class Daily(val o3: List<DailyData>,
                 val pm10: List<DailyData>,
                 val pm25: List<DailyData>)
