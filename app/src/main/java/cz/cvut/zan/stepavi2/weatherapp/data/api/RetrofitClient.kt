package cz.cvut.zan.stepavi2.weatherapp.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    private const val GEO_BASE_URL = "https://nominatim.openstreetmap.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "WeatherApp/1.0 (your.email@example.com)")
                .build()
            chain.proceed(request)
        }
        .build()

    val geoApi: GeoApi by lazy {
        Retrofit.Builder()
            .baseUrl(GEO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoApi::class.java)
    }
}