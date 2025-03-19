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
    private val _weather = MutableStateFlow(Weather(city, null, null))
    val weather: StateFlow<Weather> = _weather

    init {
        loadWeather()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            println("Loading weather for city: $city in DetailViewModel")
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val condition = when (response.currentWeather.weatherCode) {
                    0 -> "Clear"
                    1, 2, 3 -> "Cloudy"
                    61, 63, 65 -> "Rain"
                    else -> "Unknown"
                }
                val newWeather = Weather(
                    city = city,
                    condition = condition,
                    temperature = response.currentWeather.temperature
                )
                println("Weather updated in DetailViewModel: $newWeather")
                _weather.value = newWeather
            }.onFailure {
                val errorWeather = Weather(city = city, condition = "Error", temperature = null)
                println("Weather error in DetailViewModel: $errorWeather")
                _weather.value = errorWeather
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