package cz.cvut.zan.stepavi2.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("hourly") val hourly: HourlyData,
    @SerializedName("daily") val daily: DailyData
)

data class CurrentWeather(
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    @SerializedName("time") val time: String
)

data class HourlyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("relativehumidity_2m") val humidity: List<Int>,
    @SerializedName("pressure_msl") val pressure: List<Double>,
    @SerializedName("windspeed_10m") val windspeed: List<Double>,
    @SerializedName("winddirection_10m") val winddirection: List<Int>
)

data class DailyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("sunrise") val sunrise: List<String>,
    @SerializedName("sunset") val sunset: List<String>
)