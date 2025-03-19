package cz.cvut.zan.stepavi2.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class GeoResponse(
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String
)