package cz.cvut.zan.stepavi2.weatherapp.data.repository

import android.content.Context
import cz.cvut.zan.stepavi2.weatherapp.data.api.RetrofitClient
import cz.cvut.zan.stepavi2.weatherapp.data.model.WeatherResponse
import cz.cvut.zan.stepavi2.weatherapp.util.LocationUtil

class WeatherRepository(private val context: Context) {
    suspend fun getCurrentWeather(city: String): Result<WeatherResponse> {
        return try {
            println("Fetching coordinates for city: $city")
            val geoResponse = RetrofitClient.geoApi.getCoordinates(city)
            if (geoResponse.isEmpty()) {
                throw Exception("City not found: $city")
            }
            val coordinates = geoResponse[0]
            val latitude = coordinates.latitude.toDouble()
            val longitude = coordinates.longitude.toDouble()
            println("Coordinates for $city: lat=$latitude, lon=$longitude")

            println("Fetching weather for city: $city (lat: $latitude, lon: $longitude)")
            val response = RetrofitClient.weatherApi.getCurrentWeather(latitude, longitude)
            println("Weather response: $response")
            Result.success(response)
        } catch (e: Exception) {
            println("Error fetching weather: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getWeatherForCurrentLocation(): Result<WeatherResponse> {
        val locationUtil = LocationUtil(context)
        val coordinates = locationUtil.getCurrentLocation()
            ?: return Result.failure(Exception("Location not available"))
        val (latitude, longitude) = coordinates
        return try {
            println("Fetching weather for current location (lat: $latitude, lon: $longitude)")
            val response = RetrofitClient.weatherApi.getCurrentWeather(latitude, longitude)
            println("Weather response: $response")
            Result.success(response)
        } catch (e: Exception) {
            println("Error fetching weather: ${e.message}")
            Result.failure(e)
        }
    }
}