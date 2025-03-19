package cz.cvut.zan.stepavi2.weatherapp.util

object ValidationUtil {
    fun isValidCityName(city: String): Boolean {
        if (city.trim().isEmpty()) return false
        if (city.length < 2) return false
        if (city.any { it.isDigit() }) return false
        if (city.any { !it.isLetter() && !it.isWhitespace() }) return false
        return true
    }

    fun getCityValidationError(city: String): String? {
        return when {
            city.trim().isEmpty() -> "City name cannot be empty"
            city.length < 2 -> "City name must be at least 2 characters long"
            city.any { it.isDigit() } -> "City name cannot contain numbers"
            city.any { !it.isLetter() && !it.isWhitespace() } -> "City name can only contain letters and spaces"
            else -> null
        }
    }
}