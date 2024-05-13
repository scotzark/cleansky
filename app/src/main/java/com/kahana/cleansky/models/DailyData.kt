package com.kahana.cleansky.models

import androidx.annotation.Keep

@Keep
data class DailyData(val avg: Int, val day: String, val min: Int, val max: Int)
