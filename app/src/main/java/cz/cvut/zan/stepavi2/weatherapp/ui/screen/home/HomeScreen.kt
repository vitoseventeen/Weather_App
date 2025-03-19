package cz.cvut.zan.stepavi2.weatherapp.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val weatherState = viewModel.weather.collectAsState().value

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Current Weather",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
            Text(
                text = weatherState.city ?: "Loading...",
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Temperature: ${weatherState.temperature ?: "--"}°C",
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = { viewModel.loadWeather("Prague") },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Refresh Weather")
            }
            Button(
                onClick = { onCityClick(weatherState.city ?: "Prague") },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("See Details")
            }
            Button(
                onClick = { onCityClick("search") }, // Переход на SearchScreen
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Search City")
            }
            Button(
                onClick = { onCityClick("favorites") }, // Переход на FavoritesScreen
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Favorites")
            }
            Button(
                onClick = { onCityClick("settings") }, // Переход на SettingsScreen
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Settings")
            }
        }
    }
}