package com.plcoding.weatherapp.presentation

import com.plcoding.weatherapp.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    var city:String="Yourplace",
    val day:Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
