package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager

class FavoritesViewModelFactory(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(cityRepository, weatherRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}