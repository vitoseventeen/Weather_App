package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.model.WeatherResponse
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.WeatherAlarmReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritesViewModel(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val favorites: Flow<List<String>> = cityRepository.allCities.map { cities ->
        cities.map { it.name }
    }

    private val _weatherData = MutableStateFlow<Map<String, Weather?>>(emptyMap())
    val weatherData: StateFlow<Map<String, Weather?>> = _weatherData.asStateFlow()

    val temperatureUnitFlow: Flow<String> = preferencesManager.temperatureUnitFlow

    private val _alarmResult = MutableStateFlow<String?>(null)
    val alarmResult: StateFlow<String?> = _alarmResult.asStateFlow()

    init {
        viewModelScope.launch {
            favorites.collect { cities ->
                loadWeatherForCities(cities)
            }
        }
    }

    fun addCity(cityName: String) {
        viewModelScope.launch {
            cityRepository.insertCity(cityName)
            loadWeatherForCity(cityName)
        }
    }

    fun removeCity(cityName: String) {
        viewModelScope.launch {
            cityRepository.deleteCity(cityName)
            _weatherData.value = _weatherData.value.toMutableMap().apply {
                remove(cityName)
            }
        }
    }

    fun refreshAllWeather(cities: List<String>) {
        viewModelScope.launch {
            loadWeatherForCities(cities)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setWeatherAlarm(city: String, timeInMillis: Long) {
        val context = preferencesManager.context
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WeatherAlarmReceiver::class.java).apply {
            putExtra("city", city)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            city.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            Log.d("FavoritesViewModel", "Setting alarm for $city at ${dateFormat.format(timeInMillis)} (timeInMillis: $timeInMillis)")
            Log.d("FavoritesViewModel", "Current time: ${dateFormat.format(System.currentTimeMillis())}")

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("FavoritesViewModel", "Alarm successfully set for $city at ${dateFormat.format(timeInMillis)}")
            _alarmResult.value = "Alarm successfully added for $city"
        } catch (e: Exception) {
            Log.e("FavoritesViewModel", "Failed to set alarm for $city: ${e.message}")
            _alarmResult.value = "Failed to set alarm: ${e.message}"
        }
    }

    fun clearAlarmResult() {
        _alarmResult.value = null
    }

    private suspend fun loadWeatherForCities(cities: List<String>) {
        val updatedWeatherData = _weatherData.value.toMutableMap()
        cities.forEach { city ->
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val weather = response.toWeather(city)
                updatedWeatherData[city] = weather
            }.onFailure { e ->
                updatedWeatherData[city] = null
            }
        }
        _weatherData.value = updatedWeatherData
    }

    private suspend fun loadWeatherForCity(city: String) {
        val result = weatherRepository.getCurrentWeather(city)
        val updatedWeatherData = _weatherData.value.toMutableMap()
        result.onSuccess { response ->
            val weather = response.toWeather(city)
            updatedWeatherData[city] = weather
        }.onFailure { e ->
            updatedWeatherData[city] = null
        }
        _weatherData.value = updatedWeatherData
    }
}

fun WeatherResponse.toWeather(city: String): Weather {
    return Weather(
        city = city,
        condition = when (currentWeather.weatherCode) {
            0 -> "Clear"
            1, 2, 3 -> "Cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow"
            95 -> "Thunderstorm"
            else -> "Unknown"
        },
        temperature = currentWeather.temperature,
        weatherCode = currentWeather.weatherCode,
        windspeed = hourly.windspeed.firstOrNull() ?: 0.0,
        winddirection = hourly.winddirection.firstOrNull() ?: 0,
        pressure = hourly.pressure.firstOrNull() ?: 0.0,
        humidity = hourly.humidity.firstOrNull() ?: 0,
        sunrise = daily.sunrise.firstOrNull() ?: "N/A",
        sunset = daily.sunset.firstOrNull() ?: "N/A"
    )
}