package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository

class FavoritesViewModelFactory(private val cityRepository: CityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(cityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}