package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager

class ForecastViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForecastViewModel(
                WeatherRepository(context),
                PreferencesManager(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}