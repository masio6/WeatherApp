package com.plcoding.weatherapp.presentation

import android.content.Context
import android.location.Geocoder
import android.print.PrinterInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.weatherapp.domain.location.LocationTracker
import com.plcoding.weatherapp.domain.repository.WeatherRepository
import com.plcoding.weatherapp.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,

): ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun loadWeatherInfo(context: Context,city: String,selecteddateid:Int) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
          var cityName= city
            if (city == "mylocation") {
                locationTracker.getCurrentLocation()?.let { location ->
                    println("tutaj repo")
              println(repository)
                    when (val result =
                        repository.getWeatherData(location.latitude, location.longitude)) {
                        is Resource.Success -> {


                            val geocoder = Geocoder(context, Locale.getDefault())

                            try {
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)


                                    cityName = addresses[0].locality
                                    if (cityName != null) {

                                        println("Nazwa miasta na podstawie współrzędnych to: $cityName")
                                    } else {
                                        println("Nie udało się uzyskać nazwy miasta na podstawie współrzędnych.")
                                    }

                            } catch (e: IOException) {
                                println("Wystąpił błąd podczas próby uzyskania informacji na podstawie współrzędnych.")
                            }


                            state = state.copy(
                                weatherInfo = result.data,
                                day = selecteddateid,
                                city = cityName,
                                isLoading = false,
                                error = null

                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                weatherInfo = null,

                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                } ?: kotlin.run {
                    state = state.copy(
                        isLoading = false,
                        error = "Couldn't retrieve location. Make sure to grant permission and enable GPS."
                    )
                }
            }
            else{


                try {
                    println("tutaj repo")
                    println(repository)

                    var gc = Geocoder(context, Locale.getDefault())
                    var addresses = gc.getFromLocationName(city, 2)

                    if (addresses.isNotEmpty()) {
                        var addres = addresses.get(0)



                    when (val result =
                        repository.getWeatherData(addres.latitude, addres.longitude)) {
                        is Resource.Success -> {
                            state = state.copy(
                                weatherInfo = result.data,
                                isLoading = false,
                                city = city,
                                day = selecteddateid,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                weatherInfo = null,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                }
                else{


                        state = state.copy(
                            weatherInfo = null,
                            isLoading = false,
                            error = "The selected city was not found."
                        )
                    }
                }
             catch (e: IOException) {
            println("An error occurred while trying to obtain coordinates.")
        }





            }
        }
    }
}