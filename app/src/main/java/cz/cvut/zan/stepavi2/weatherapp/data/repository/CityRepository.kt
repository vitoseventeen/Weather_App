package cz.cvut.zan.stepavi2.weatherapp.data.repository

import cz.cvut.zan.stepavi2.weatherapp.data.dao.CityDao
import cz.cvut.zan.stepavi2.weatherapp.data.entity.CityEntity
import kotlinx.coroutines.flow.Flow

class CityRepository(private val cityDao: CityDao) {
    val allCities: Flow<List<CityEntity>> = cityDao.getAllCities()

    suspend fun insertCity(cityName: String) {
        cityDao.insertCity(CityEntity(cityName))
    }

    suspend fun deleteCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }
}