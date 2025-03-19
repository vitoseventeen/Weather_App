package cz.cvut.zan.stepavi2.weatherapp.data.repository

import cz.cvut.zan.stepavi2.weatherapp.data.api.RetrofitClient
import cz.cvut.zan.stepavi2.weatherapp.data.model.WeatherResponse

class WeatherRepository {
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
}