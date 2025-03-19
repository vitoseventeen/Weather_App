package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoritesViewModel(private val cityRepository: CityRepository) : ViewModel() {
    val favorites: Flow<List<String>> = cityRepository.allCities.map { cities ->
        cities.map { it.name }
    }

    fun addCity(cityName: String) {
        viewModelScope.launch {
            cityRepository.insertCity(cityName)
        }
    }

    fun removeCity(cityName: String) {
        viewModelScope.launch {
            cityRepository.deleteCity(cityName)
        }
    }
}