package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailViewModel(
    private val city: String,
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val temperatureUnitFlow: Flow<String> = preferencesManager.temperatureUnitFlow

    init {
        loadWeather()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val condition = when (response.currentWeather.weatherCode) {
                    0 -> "Clear"
                    1, 2, 3 -> "Cloudy"
                    45, 48 -> "Fog"
                    51, 53, 55 -> "Drizzle"
                    61, 63, 65 -> "Rain"
                    71, 73, 75 -> "Snow"
                    95 -> "Thunderstorm"
                    else -> "Unknown"
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val currentWeatherTime = try {
                    dateFormat.parse(response.currentWeather.time)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }

                val currentHourIndex = response.hourly.time.indexOfFirst { timeStr ->
                    try {
                        val hourlyTime = dateFormat.parse(timeStr)?.time ?: 0L
                        hourlyTime >= currentWeatherTime
                    } catch (e: Exception) {
                        false
                    }
                }.takeIf { it >= 0 } ?: (response.hourly.time.size - 1)

                _weather.value = Weather(
                    city = city,
                    condition = condition,
                    temperature = response.currentWeather.temperature,
                    weatherCode = response.currentWeather.weatherCode,
                    windspeed = response.hourly.windspeed[currentHourIndex],
                    winddirection = response.hourly.winddirection[currentHourIndex],
                    pressure = response.hourly.pressure[currentHourIndex],
                    humidity = response.hourly.humidity[currentHourIndex],
                    sunrise = response.daily.sunrise.firstOrNull() ?: "N/A",
                    sunset = response.daily.sunset.firstOrNull() ?: "N/A"
                )
                _error.value = null
            }.onFailure { e ->
                _weather.value = null
                _error.value = if (e.message?.contains("City not found") == true) {
                    "City not found"
                } else {
                    "Error fetching weather"
                }
            }
        }
    }
}