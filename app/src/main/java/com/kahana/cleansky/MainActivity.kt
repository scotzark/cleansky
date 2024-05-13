package com.kahana.cleansky

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kahana.cleansky.databinding.ActivityMainBinding
import com.kahana.cleansky.databinding.ViewDailyAirQualityBinding
import com.kahana.cleansky.models.AirQualityData
import com.kahana.cleansky.models.DailyData
import com.kahana.cleansky.network.AirQualityApiImp
import com.kahana.cleansky.network.AirQualityDataSource
import com.kahana.cleansky.util.AirQualityAlgorithm
import com.kahana.cleansky.util.PositioningManager
import com.kahana.cleansky.util.visible
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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission()

        errorMessage.observe(this, Observer {
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = it
        })

        viewModel.airQualityData.observe(this, Observer {
            val data = it
            displayData(data)
        })

        location.currentLocation.observe(this, Observer{
            val location = it
            if (location != null) {
                searchForAirQualityData(location)
            }
            else {
                logErrorMessage(resources.getString(R.string.error_cannot_determine_location))
            }
        })

        binding.search.setOnClickListener {
            val location = Location("")
            location.latitude =  binding.latitude.text.toString().toDouble()
            location.longitude = binding.longitude.text.toString().toDouble()
           searchForAirQualityData(location)
        }
    }

    private fun searchForAirQualityData(location: Location) {
        binding.progressBar.visible = true
        binding.mainLayout.visible = false
        viewModel.getAirQualityData(location)
    }

    private fun displayData(data: AirQualityData) {
        binding.progressBar.visible = false
        binding.mainLayout.visible = true
        binding.city.text = resources.getString(R.string.request_for_city, data.city.name)
        binding.attributions.text = resources.getString(R.string.air_quality_reporting_stations, data.attributions.map { it.name })

        // Get Ozone for Today
        val airQualityToday = getAirqualityForToday(data)
        val currentIndex = data.forecast.daily.o3.indexOf(airQualityToday)

        if (airQualityToday != null) {

            configureCard(airQualityToday, binding.today)

            // Get Ozone for Yesterday
            if (currentIndex - 1 > 0) {
                val airQualityYesterday = data.forecast.daily.o3[currentIndex - 1]
                configureCard(airQualityYesterday, binding.yesterday)
            }

            // Get Ozone for Tomorrow

            if (currentIndex + 1 < data.forecast.daily.o3.size) {
                val airQualityTomorrow = data.forecast.daily.o3[currentIndex + 1]
                configureCard(airQualityTomorrow, binding.tomorrow)
            }
        }
        else {
            logErrorMessage(resources.getString(R.string.error_no_data_for_todays_date))
        }
    }

    private fun getAirqualityForToday(data: AirQualityData): DailyData? {
        val today = todaysDate()
        return data.forecast.daily.o3.firstOrNull { it.day == today }
    }

     private fun configureCard(airQuality: DailyData, view: ViewDailyAirQualityBinding) {
         val severity = AirQualityAlgorithm.calcualeResultforType(AirQualityAlgorithm.AirQualityType.O3, airQuality.avg ?: 0)
         view.date.text = resources.getString(R.string.air_quality_for_date, airQuality.day)
         view.ozoneLevel.text = resources.getString(R.string.ozone_level, airQuality.avg)
         view.description.text = "(${severity.name})"
        setCircleColor(view.severityCircle, severity.color)
    }

    private fun setCircleColor(image: ImageView,color: Int) {
        val background = image.background
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

        if (requestCode == PERMISSION_RESULT && grantResults.first().equals(0)) {
            location.getLocation()
        }
        else {
            logErrorMessage(resources.getString(R.string.error_no_location_permission))
        }
    }

    private fun logErrorMessage(msg: String) {
        binding.progressBar.visible = false
        binding.mainLayout.visible = true
        _errorMessage.value = msg
    }
}
