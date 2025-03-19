package cz.cvut.zan.stepavi2.weatherapp.domain.model

data class Weather(
    val city: String?,
    val condition: String?,
    val temperature: Double?
)