package cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast.ForecastDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _forecastCityInput = MutableStateFlow("")
    val forecastCityInput: StateFlow<String> = _forecastCityInput.asStateFlow()

    private val _forecastCityToDisplay = MutableStateFlow("")
    val forecastCityToDisplay: StateFlow<String> = _forecastCityToDisplay.asStateFlow()

    private val _forecastState = MutableStateFlow<List<ForecastDay>?>(null)
    val forecastState: StateFlow<List<ForecastDay>?> = _forecastState.asStateFlow()

    private val _favoritesCityInput = MutableStateFlow("")
    val favoritesCityInput: StateFlow<String> = _favoritesCityInput.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _homeCityInput = MutableStateFlow("")
    val homeCityInput: StateFlow<String> = _homeCityInput.asStateFlow()

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

    fun clearError() {
        _error.value = null
    }

    fun updateHomeCityInput(city: String) {
        _homeCityInput.value = city
    }

    suspend fun checkCityExists(city: String): Boolean {
        val result = weatherRepository.getCurrentWeather(city)
        return if (result.isSuccess) {
            _error.value = null
            true
        } else {
            val exception = result.exceptionOrNull()
            _error.value = if (exception?.message?.contains("City not found") == true) {
                "City not found: $city"
            } else {
                "Error checking city: ${exception?.message}"
            }
            false
        }
    }

    fun refreshForecastForCity(city: String) {
        viewModelScope.launch {
            val result = weatherRepository.getForecast(city)
            result.onSuccess { forecast ->
                _forecastState.value = forecast
                _forecastCityToDisplay.value = city
            }.onFailure { e ->
                _error.value = "Error loading forecast: ${e.message}"
            }
        }
    }
}