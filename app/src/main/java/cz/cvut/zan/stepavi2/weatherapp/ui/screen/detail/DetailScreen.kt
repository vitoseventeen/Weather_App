package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    city: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory(city))
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
                text = "Weather in $city",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Condition: ${weatherState.condition ?: "Unknown"}",
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Temperature: ${weatherState.temperature ?: "--"}°C",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}