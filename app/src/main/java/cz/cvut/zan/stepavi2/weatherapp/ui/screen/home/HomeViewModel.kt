package cz.cvut.zan.stepavi2.weatherapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.WeatherUtils
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

    fun loadWeather(city: String) {
        viewModelScope.launch {
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                _weather.value = WeatherUtils.createWeatherFromResponse(city, response)
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

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            val result = weatherRepository.getWeatherForCurrentLocation()
            result.onSuccess { response ->
                val city = weatherRepository.getCityFromCoordinates(
                    response.latitude,
                    response.longitude
                ).getOrNull()?.address?.getCityName() ?: "Unknown Location"
                _weather.value = WeatherUtils.createWeatherFromResponse(city, response)
                _error.value = null
            }.onFailure { e ->
                _weather.value = null
                _error.value = "Error fetching weather for current location"
            }
        }
    }
}