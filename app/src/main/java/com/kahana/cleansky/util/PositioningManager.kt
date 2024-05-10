package com.kahana.cleansky.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PositioningManager(private val activity: Activity) {
    var locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?>
        get() = _currentLocation

    @SuppressLint("MissingPermission")
    fun getLocation() {
       _currentLocation.value = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

}