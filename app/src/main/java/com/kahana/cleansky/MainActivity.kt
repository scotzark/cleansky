package com.kahana.cleansky

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kahana.cleansky.databinding.ActivityMainBinding
import com.kahana.cleansky.models.AirQualityData
import com.kahana.cleansky.network.AirQualityApiImp
import com.kahana.cleansky.network.AirQualityDataSource
import com.kahana.cleansky.util.AirQualityAlgorithm
import com.kahana.cleansky.util.PositioningManager
import com.kahana.cleansky.viewmodel.AirQualityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_RESULT = 1001
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        AirQualityViewModel(AirQualityApiImp( AirQualityDataSource.apiService))
    }

    private val location by lazy {
        PositioningManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission()

        viewModel.airQualityData.observe(this, Observer {
            val data = it
            displayData(data)
        })

        location.currentLocation.observe(this, Observer{
            val location = it
            if (location != null) {
                viewModel.getAirQualityData(location)
            }
        })

    }

    private fun displayData(data: AirQualityData) {
        binding.city.text = resources.getString(R.string.request_for_city, data.city.name)
        binding.attributions.text = resources.getString(R.string.air_quality_reporting_stations, data.attributions.map { it.name })

        // Get Ozone for Today
        val today = todaysDate()
        val airQualityToday = data.forecast.daily.o3.firstOrNull { it.day == today }
        val currentIndex = data.forecast.daily.o3.indexOf(airQualityToday)

        binding.today.date.text = resources.getString(R.string.air_quality_for_date, airQualityToday?.day ?: "")
        binding.today.ozoneLevel.text = resources.getString(R.string.ozone_level, airQualityToday?.avg)

        val severity = AirQualityAlgorithm.calcualeResultforType(AirQualityAlgorithm.AirQualityType.O3, airQualityToday?.avg ?: 0)
        setCircleColor(binding.today.severityCircle, severity.color)
        // Get Ozone for Yesterday

        if (currentIndex - 1 > 0) {
            val airQualityYesterday = data.forecast.daily.o3[currentIndex-1]
            binding.yesterday.date.text = resources.getString(R.string.air_quality_for_date, airQualityYesterday.day)
            binding.yesterday.ozoneLevel.text = resources.getString(R.string.ozone_level, airQualityYesterday.avg)
            val severity = AirQualityAlgorithm.calcualeResultforType(AirQualityAlgorithm.AirQualityType.O3, airQualityYesterday.avg ?: 0)
            setCircleColor(binding.today.severityCircle, severity.color)
        }

        // Get Ozone for Tomorrow

        if (currentIndex + 1 < data.forecast.daily.o3.size) {
            val airQualityTomorrow = data.forecast.daily.o3[currentIndex+1]
            binding.tomorrow.date.text = resources.getString(R.string.air_quality_for_date, airQualityTomorrow.day)
            binding.tomorrow.ozoneLevel.text = resources.getString(R.string.ozone_level, airQualityTomorrow.avg)
            val severity = AirQualityAlgorithm.calcualeResultforType(AirQualityAlgorithm.AirQualityType.O3, airQualityTomorrow.avg)
            setCircleColor(binding.today.severityCircle, severity.color)
        }
    }

    private fun setCircleColor(image: ImageView,color: Int) {
        val background = image.background
        val green = R.color.green
        (background as GradientDrawable).setColor(ContextCompat.getColor(this, color))
    }
    private fun todaysDate(): String {
        val dateFormat = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        }
        else {
            location.getLocation()

        }
    }
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_RESULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_RESULT) {
            location.getLocation()
        }
    }
}
