package com.plcoding.weatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun WeatherForecast(
    state: WeatherState,
    modifier: Modifier = Modifier
) {

    var dayindex = state.day

    var currentDate = Calendar.getInstance()
    var calendardate = calculateDateFromDayDifference(currentDate, dayindex)

    val day = calendardate.get(Calendar.DAY_OF_MONTH)
    val month = calendardate.get(Calendar.MONTH) + 1 // Miesiące w Calendar są indeksowane od 0
    val year = calendardate.get(Calendar.YEAR)

    state.weatherInfo?.weatherDataPerDay?.get(dayindex)?.let { data->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = " ${day}.${month}.$year",
                fontSize = 15.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            LazyRow(content = {
                items(data) { weatherData ->
                    HourlyWeatherDisplay(
                        weatherData = weatherData,
                        modifier = Modifier
                            .height(100.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            })
        }
    }
}

fun calculateDateFromDayDifference(today: Calendar, dayDifference: Int): Calendar {
    val selectedDate = today.clone() as Calendar
    selectedDate.add(Calendar.DAY_OF_MONTH, dayDifference)
    return selectedDate
}