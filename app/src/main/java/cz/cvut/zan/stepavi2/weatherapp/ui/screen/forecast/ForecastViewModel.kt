package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastViewModel(
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _forecast = MutableStateFlow<List<ForecastDay>?>(null)
    val forecast: StateFlow<List<ForecastDay>?> = _forecast.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val temperatureUnitFlow: Flow<String> = preferencesManager.temperatureUnitFlow

    fun loadForecast(city: String) {
        viewModelScope.launch {
            println("Loading forecast for city: $city")
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                println("Forecast response: $response")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val forecastDays = response.daily.time.mapIndexedNotNull { index, time ->
                    try {
                        val date = dateFormat.parse(time)
                        val displayDate = displayFormat.format(date!!)
                        ForecastDay(
                            date = displayDate,
                            minTemperature = response.daily.temperatureMin[index],
                            maxTemperature = response.daily.temperatureMax[index],
                            weatherCode = response.daily.weatherCode[index]
                        )
                    } catch (e: Exception) {
                        println("Error parsing date at index $index: ${e.message}")
                        null
                    }
                }.take(7)

                println("Parsed forecast days: $forecastDays")
                _forecast.value = forecastDays
                _error.value = null
            }.onFailure { e ->
                println("Failed to load forecast: ${e.message}")
                _forecast.value = null
                _error.value = if (e.message?.contains("City not found") == true) {
                    "City not found"
                } else {
                    "Error fetching forecast: ${e.message}"
                }
            }
        }
    }
}

