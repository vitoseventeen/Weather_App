package cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import cz.cvut.zan.stepavi2.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _weather = MutableStateFlow(Weather(null, null, null))
    val weather: StateFlow<Weather> = _weather

    fun loadWeather(city: String) {
        _weather.value = Weather(city, "Sunny", 20.0)
    }
}