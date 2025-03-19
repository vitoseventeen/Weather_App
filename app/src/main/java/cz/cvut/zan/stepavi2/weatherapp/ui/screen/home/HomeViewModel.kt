package cz.cvut.zan.stepavi2.weatherapp.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.api.RetrofitClient
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val temperatureUnitFlow: Flow<String> = preferencesManager.temperatureUnitFlow

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            val result = weatherRepository.getWeatherForCurrentLocation()
            result.onSuccess { response ->
                val city = getCityFromCoordinates(response.latitude, response.longitude)
                val condition = when (response.currentWeather.weatherCode) {
                    0 -> "Clear"
                    1, 2, 3 -> "Cloudy"
                    61, 63, 65 -> "Rain"
                    else -> "Unknown"
                }
                _weather.value = Weather(city, condition, response.currentWeather.temperature)
                _error.value = null
            }.onFailure {
                _error.value = "Unable to get current location"
            }
        }
    }

    fun loadWeather(city: String) {
        viewModelScope.launch {
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val condition = when (response.currentWeather.weatherCode) {
                    0 -> "Clear"
                    1, 2, 3 -> "Cloudy"
                    61, 63, 65 -> "Rain"
                    else -> "Unknown"
                }
                _weather.value = Weather(city, condition, response.currentWeather.temperature)
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

    private suspend fun getCityFromCoordinates(latitude: Double, longitude: Double): String {
        val geoResponse = RetrofitClient.geoApi.getCityFromCoordinates(latitude, longitude)
        return geoResponse.address.getCityName() ?: "Unknown city"
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(
                        WeatherRepository(context),
                        PreferencesManager(context)
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}