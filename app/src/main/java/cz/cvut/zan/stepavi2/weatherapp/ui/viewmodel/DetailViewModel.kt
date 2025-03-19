package cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailViewModel(private val city: String) : ViewModel() {
    private val _weather = MutableStateFlow(Weather(city, null, null))
    val weather: StateFlow<Weather> = _weather

    init {
        loadWeather()
    }

    private fun loadWeather() {
        _weather.value = Weather(city, "Cloudy", 18.0)
    }

    class Factory(private val city: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(city) as T
        }
    }
}