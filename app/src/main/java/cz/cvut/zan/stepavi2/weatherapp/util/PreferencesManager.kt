package cz.cvut.zan.stepavi2.weatherapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")
        const val CELSIUS = "Celsius"
        const val FAHRENHEIT = "Fahrenheit"
    }

    val temperatureUnitFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] ?: CELSIUS
        }

    suspend fun saveTemperatureUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = unit
        }
    }
}