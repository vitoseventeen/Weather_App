package cz.cvut.zan.stepavi2.weatherapp.util

import cz.cvut.zan.stepavi2.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Locale

object WeatherUtils {
    fun convertTemperature(temp: Double?, unit: String): Double? {
        return temp?.let {
            if (unit == PreferencesManager.FAHRENHEIT) {
                it * 9 / 5 + 32
            } else {
                it
            }
        }
    }

    fun getTemperatureUnitSymbol(unit: String): String {
        return if (unit == PreferencesManager.FAHRENHEIT) "°F" else "°C"
    }



    fun getWeatherIcon(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.ic_sunny
            1, 2, 3 -> R.drawable.ic_cloudy
            45, 48 -> R.drawable.ic_mist
            51, 53, 55 -> R.drawable.ic_drizzle
            61, 63, 65 -> R.drawable.ic_rain
            71, 73, 75 -> R.drawable.ic_snow
            95 -> R.drawable.ic_thunderstorm
            else -> R.drawable.ic_sunny
        }
    }

    fun formatTime(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(time)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "N/A"
        }
    }

    fun getWindDirection(degrees: Int): String {
        return when (degrees) {
            in 0..22, in 338..360 -> "N"
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..337 -> "NW"
            else -> "Unknown"
        }
    }
}