package cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _weather = MutableStateFlow(Weather(city = "", condition = null, temperature = null))
    val weather: StateFlow<Weather> = _weather

    fun loadWeather(city: String) {
        viewModelScope.launch {
            println("Loading weather for city: $city")
            val result = weatherRepository.getCurrentWeather(city)
            result.onSuccess { response ->
                val condition = when (response.currentWeather.weatherCode) {
                    0 -> "Clear"
                    1, 2, 3 -> "Cloudy"
                    61, 63, 65 -> "Rain"
                    else -> "Unknown"
                }
                val newWeather = Weather(
                    city = city, // Пока используем переданный город, так как Open-Meteo не возвращает название
                    condition = condition,
                    temperature = response.currentWeather.temperature
                )
                println("Weather updated: $newWeather")
                _weather.value = newWeather
            }.onFailure {
                val errorWeather = Weather(city = city, condition = "Error", temperature = null)
                println("Weather error: $errorWeather")
                _weather.value = errorWeather
            }
        }
    }
}