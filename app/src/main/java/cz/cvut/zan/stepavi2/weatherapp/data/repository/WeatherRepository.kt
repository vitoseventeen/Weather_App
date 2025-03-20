package cz.cvut.zan.stepavi2.weatherapp.data.repository

import android.content.Context
import cz.cvut.zan.stepavi2.weatherapp.data.api.RetrofitClient
import cz.cvut.zan.stepavi2.weatherapp.data.model.ReverseGeoResponse
import cz.cvut.zan.stepavi2.weatherapp.data.model.WeatherResponse
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast.ForecastDay
import cz.cvut.zan.stepavi2.weatherapp.util.LocationUtil
import java.text.SimpleDateFormat
import java.util.Locale

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

    suspend fun getCityFromCoordinates(latitude: Double, longitude: Double): Result<ReverseGeoResponse> {
        return try {
            val response = RetrofitClient.geoApi.getCityFromCoordinates(latitude, longitude)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getForecast(city: String): Result<List<ForecastDay>> {
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

            println("Fetching forecast for city: $city (lat: $latitude, lon: $longitude)")
            val response = RetrofitClient.weatherApi.getCurrentWeather(latitude, longitude)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val forecastDays = response.daily.time.mapIndexedNotNull { index, time ->
                try {
                    val date = dateFormat.parse(time)
                    val displayDate = displayFormat.format(date!!)
                    ForecastDay(
                        date = displayDate,
                        minTemperature = response.daily.temperatureMin[index],
                        maxTemperature = response.daily.temperatureMax[index],
                        weatherCode = response.daily.weatherCode[index]
                    )
                } catch (e: Exception) {
                    println("Error parsing date at index $index: ${e.message}")
                    null
                }
            }.take(7)
            println("Forecast response: $forecastDays")
            Result.success(forecastDays)
        } catch (e: Exception) {
            println("Error fetching forecast: ${e.message}")
            Result.failure(e)
        }
    }
}