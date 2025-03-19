package cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritesViewModel : ViewModel() {
    private val _favorites = MutableStateFlow(listOf("Prague", "Berlin", "London"))
    val favorites: StateFlow<List<String>> = _favorites
}