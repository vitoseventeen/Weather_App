package cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _homeCityInput = MutableStateFlow("")
    val homeCityInput: StateFlow<String> = _homeCityInput.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _favoritesCityInput = MutableStateFlow(TextFieldValue(""))
    val favoritesCityInput: StateFlow<TextFieldValue> = _favoritesCityInput.asStateFlow()

    fun updateHomeCityInput(city: String) {
        _homeCityInput.value = city
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFavoritesCityInput(city: TextFieldValue) {
        _favoritesCityInput.value = city
    }

    fun clearFavoritesCityInput() {
        _favoritesCityInput.value = TextFieldValue("")
    }
}