package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

data class ForecastDay(
    val date: String,
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val weatherCode: Int
)