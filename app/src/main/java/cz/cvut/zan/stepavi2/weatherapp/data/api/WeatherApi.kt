package cz.cvut.zan.stepavi2.weatherapp.data.api

import cz.cvut.zan.stepavi2.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,relativehumidity_2m,pressure_msl,windspeed_10m,winddirection_10m",
        @Query("daily") daily: String = "sunrise,sunset"
    ): WeatherResponse
}