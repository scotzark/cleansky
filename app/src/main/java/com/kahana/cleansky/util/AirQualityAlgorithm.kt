package com.kahana.cleansky.util

import com.kahana.cleansky.R

object AirQualityAlgorithm {
    enum class AirQualityType {
        O3, PM25, PM10
    }

    enum class AirQualityResult(val color: Int) {
        NA(R.color.black),
        EXCELLENT(R.color.green),
        FINE(R.color.green),
        MODERATE(R.color.yellow),
        POOR(R.color.yellow),
        VERY_POOR(R.color.red),
        SEVERE(R.color.red)
    }

    fun calcualeResultforType(type: AirQualityType, value: Int): AirQualityResult {
        if (value == 0) return AirQualityResult.NA
        return when (type) {
            AirQualityType.O3 -> {
                when (value)  {
                    in 0..49 -> AirQualityResult.EXCELLENT
                    in 50..99 -> AirQualityResult.FINE
                    in 100..129 -> AirQualityResult.MODERATE
                    in 130..239 -> AirQualityResult.POOR
                    in 240..379 -> AirQualityResult.VERY_POOR
                    else -> AirQualityResult.SEVERE

                }
            }

            else -> AirQualityResult.VERY_POOR
        }
    }

}