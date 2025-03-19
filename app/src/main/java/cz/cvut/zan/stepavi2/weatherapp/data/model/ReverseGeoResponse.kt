package cz.cvut.zan.stepavi2.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class ReverseGeoResponse(
    @SerializedName("address") val address: Address
)

data class Address(
    @SerializedName("city") val city: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?
) {
    fun getCityName(): String? = city ?: town ?: village
}