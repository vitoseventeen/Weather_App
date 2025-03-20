package cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared

import androidx.lifecycle.ViewModel
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast.ForecastDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _forecastCityInput = MutableStateFlow("")
    val forecastCityInput: StateFlow<String> = _forecastCityInput.asStateFlow()

    private val _forecastCityToDisplay = MutableStateFlow("")
    val forecastCityToDisplay: StateFlow<String> = _forecastCityToDisplay.asStateFlow()

    private val _forecastState = MutableStateFlow<List<ForecastDay>?>(null)
    val forecastState: StateFlow<List<ForecastDay>?> = _forecastState.asStateFlow()

    private val _favoritesCityInput = MutableStateFlow("")
    val favoritesCityInput: StateFlow<String> = _favoritesCityInput.asStateFlow()

    fun updateForecastCityInput(city: String) {
        _forecastCityInput.value = city
    }

    fun updateForecastCityToDisplay(city: String) {
        _forecastCityToDisplay.value = city
    }

    fun updateForecastState(forecast: List<ForecastDay>?) {
        _forecastState.value = forecast
    }

    fun updateFavoritesCityInput(city: String) {
        _favoritesCityInput.value = city
    }

    fun clearFavoritesCityInput() {
        _favoritesCityInput.value = ""
    }
}