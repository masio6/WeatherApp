package com.plcoding.weatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.location.Geocoder
//import android.os.Bundle
import android.widget.DatePicker
//import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.weatherapp.R
import com.plcoding.weatherapp.presentation.ui.theme.DarkBlue
import com.plcoding.weatherapp.presentation.ui.theme.DeepBlue
import com.plcoding.weatherapp.presentation.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    var selectedDay:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var context = this
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.loadWeatherInfo(context = context,"mylocation",selectedDay)
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
        setContent {

            val minDate = Calendar.getInstance()
            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.DAY_OF_MONTH, 7)


            Box(


            ) {



                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBlue)
                ) {


                        showDatePicker(context,minDate, maxDate)

                            Spacer(modifier = Modifier.size(10.dp))










                    WeatherCard(
                        state = viewModel.state,
                        backgroundColor = DeepBlue
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    WeatherForecast(state = viewModel.state)
                }
                if (viewModel.state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                viewModel.state.error?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }


            }
        }
    }


    @Composable
    fun showDatePicker(context: Context, minDate: Calendar, maxDate: Calendar) {
        val currentDate = remember { Calendar.getInstance() }
        val selectedDate = remember { mutableStateOf(currentDate) }
        var cityName by remember { mutableStateOf("") }
        var textState by remember { mutableStateOf("") }
        var isSearching by remember { mutableStateOf(false) }
var city:String=""
        var citycoordinates : String = ""
        val datePickerDialog = remember {
            DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val selected = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    if (selected >= minDate && selected <= maxDate) {
                        selectedDate.value = selected
                        val difference = (selected.timeInMillis - currentDate.timeInMillis) / (1000 * 60 * 60 * 24)
                        selectedDay = difference.toInt()
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
        }

        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        Column(
            modifier = Modifier.height(90.dp),
        ) {
            Text(
                text = "Wybrana Data: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}/${
                    selectedDate.value.get(
                        Calendar.MONTH
                    ) + 1
                }/${selectedDate.value.get(Calendar.YEAR)}",
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxSize()

            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(DeepBlue, shape = CircleShape),

                    contentAlignment = Alignment.Center
                ) {
                IconButton(
                    onClick = {
                        datePickerDialog.show()

                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "Przycisk Kalendarz",
                        tint = Color.White
                    )
                }}
                Spacer(modifier = Modifier.weight(0.8f))

                TextInputDemo(textState) { newTextInput ->
                    textState = newTextInput
                }
                Spacer(modifier = Modifier.weight(0.8f))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(DeepBlue, shape = CircleShape),

                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            city = textState
                            if(city=="" || city == null){
                                city="mylocation"
                            }
                            println(textState)
                            viewModel.loadWeatherInfo(context = context, city, selectedDay)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Wyszukaj",
                            tint = Color.White
                        )
                    }
                }

                    Text(
                        text = citycoordinates,

                        color = Color.White
                    )


            }
           }
    }
@Composable
    fun TextInputDemo(text: String, onTextChange: (String) -> Unit) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .width(200.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(30.dp)
                )
        ) {
            var currentText by remember { mutableStateOf(text) } // Dodaj zmienną stanu

            BasicTextField(
                value = currentText,
                onValueChange = {
                    currentText = it // Aktualizuj wartość zmiennej stanu
                    onTextChange(it) // Aktualizuj wartość tekstu za pomocą funkcji przekazanej jako parametr
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.White,
                    )
            )
        }
    }
//    @Composable
//    fun TextInputDemo(text: String, onTextChange: (String) -> Unit) {
//        Box(
//            modifier = Modifier
//                .padding(5.dp)
//                .width(200.dp)
//                .background(
//                    color = Color.White,
//                    shape = RoundedCornerShape(30.dp)
//                )
//        ) {
//            Column {
//                BasicTextField(
//                    value = text,
//                    onValueChange = {
//                        onTextChange(it) // Aktualizuj wartość tekstu za pomocą funkcji przekazanej jako parametr
//                    },
//                    textStyle = androidx.compose.ui.text.TextStyle(
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    ),
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .background(
//                            color = Color.White,
//                        )
//                )
//            }
//        }
//    }
//    @Composable
//    fun TextInputDemo() {
//        var text by remember { mutableStateOf("Podaj nazwe miasta") }
//        var newText = ""
//        Box(
//            modifier = Modifier
//                .padding(5.dp)
//                .width(100.dp)
//                .background(
//                    color = Color.White,
//                    shape = RoundedCornerShape(30.dp) // Zaokrąglone rogi
//                )
//        ) {
//            Column {
//                BasicTextField(
//                    value = text,
//                    onValueChange = { text = newText },
//                    textStyle = androidx.compose.ui.text.TextStyle(
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    ),
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .background(
//                            color = Color.White,
//
//                        )
//                )
//
//
//            }
//        }
//
//    }



}









