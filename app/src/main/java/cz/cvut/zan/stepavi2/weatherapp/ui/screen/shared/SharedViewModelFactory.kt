package cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository

class SharedViewModelFactory(
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}