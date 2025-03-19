package cz.cvut.zan.stepavi2.weatherapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val name: String
)