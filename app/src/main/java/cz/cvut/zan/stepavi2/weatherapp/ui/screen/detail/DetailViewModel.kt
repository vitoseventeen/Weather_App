package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val city: String,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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

    class Factory(
        private val city: String,
        private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(city, weatherRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}