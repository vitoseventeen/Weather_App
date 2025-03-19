package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.viewmodel.DetailViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager

@Composable
fun DetailScreen(
    city: String,
    paddingValues: PaddingValues,
    navController: NavController, // Добавляем NavController
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository(context)
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(city, context)
    )
    val weatherState by viewModel.weather.collectAsState()
    val errorState by viewModel.error.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium)
        ) {
            Text(
                text = stringResource(R.string.weather_in, city),
                style = MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge
            )
            if (errorState != null) {
                Text(
                    text = errorState!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeMedium
                )
            } else if (weatherState != null) {
                Text(
                    text = stringResource(
                        R.string.condition,
                        weatherState!!.condition ?: stringResource(R.string.unknown)
                    ),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeMedium
                )
                val temperature = weatherState!!.temperature?.let { temp ->
                    if (temperatureUnit == PreferencesManager.FAHRENHEIT) {
                        temp * 9 / 5 + 32
                    } else {
                        temp
                    }
                }
                Text(
                    text = stringResource(
                        R.string.temperature,
                        temperature?.let { String.format("%.1f", it) } ?: "--",
                        if (temperatureUnit == PreferencesManager.FAHRENHEIT) "°F" else "°C"
                    ),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeMedium
                )
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = Dimens.PaddingMedium)
                ) {
                    Text(
                        text = stringResource(R.string.back),
                        fontSize = Dimens.TextSizeMedium
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeMedium
                )
            }
        }
    }
}