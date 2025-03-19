package cz.cvut.zan.stepavi2.weatherapp.data.api

import cz.cvut.zan.stepavi2.weatherapp.data.model.GeoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {
    @GET("search")
    suspend fun getCoordinates(
        @Query("q") city: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<GeoResponse>
}