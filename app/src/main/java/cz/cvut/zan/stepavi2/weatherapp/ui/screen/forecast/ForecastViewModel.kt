package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val weatherRepository: WeatherRepository,
    preferencesManager: PreferencesManager,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val _forecast = MutableStateFlow<List<ForecastDay>?>(null)
    val forecast: StateFlow<List<ForecastDay>?> = _forecast.asStateFlow()

    val temperatureUnitFlow: Flow<String> = preferencesManager.temperatureUnitFlow

    fun loadForecast(city: String) {
        viewModelScope.launch {
            val currentCity = sharedViewModel.forecastCityToDisplay.value
            val savedForecast = sharedViewModel.forecastState.value

            if (city == currentCity && savedForecast != null) {
                println("Using saved forecast for city: $city")
                _forecast.value = savedForecast
            } else {
                println("Loading forecast for city: $city")
                val result = weatherRepository.getForecast(city)
                result.onSuccess { forecastDays ->
                    println("Parsed forecast days: $forecastDays")
                    _forecast.value = forecastDays
                    sharedViewModel.updateForecastState(forecastDays)
                    sharedViewModel.updateForecastCityToDisplay(city)
                }.onFailure { e ->
                    println("Failed to load forecast: ${e.message}")

                    sharedViewModel.updateForecastCityToDisplay(city)
                    sharedViewModel.updateForecastState(_forecast.value)
                }
            }
        }
    }
}