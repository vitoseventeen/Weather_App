package cz.cvut.zan.stepavi2.weatherapp.domain.model

data class Weather(
    val city: String,
    val condition: String?,
    val temperature: Double?,
    val weatherCode: Int, 
    val windspeed: Double, 
    val winddirection: Int, 
    val pressure: Double, 
    val humidity: Int, 
    val sunrise: String, 
    val sunset: String 
)