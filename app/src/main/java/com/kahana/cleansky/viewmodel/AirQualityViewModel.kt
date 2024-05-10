package com.kahana.cleansky.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kahana.cleansky.models.AirQualityData
import com.kahana.cleansky.network.AirQualityApiImp
import kotlinx.coroutines.launch

class AirQualityViewModel(private val api: AirQualityApiImp): ViewModel() {
    private val _airQualityData = MutableLiveData<AirQualityData>()
    val airQualityData: LiveData<AirQualityData>
        get() = _airQualityData

    fun getAirQualityData(location: Location) {
        viewModelScope.launch {
           val response = api.getAirQualityData(location)
            _airQualityData.postValue(response.data)
        }

    }
}