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
        private val THEME_KEY = stringPreferencesKey("theme")
        const val CELSIUS = "Celsius"
        const val FAHRENHEIT = "Fahrenheit"
        const val THEME_SYSTEM = "System"
        const val THEME_LIGHT = "Light"
        const val THEME_DARK = "Dark"
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

    val themeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: THEME_SYSTEM
        }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}