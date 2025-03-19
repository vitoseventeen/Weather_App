package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun DetailScreen(
    city: String,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository(context)
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(city, weatherRepository)
    )
    val weatherState by viewModel.weather.collectAsState()
    val errorState by viewModel.error.collectAsState()

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
                Text(
                    text = stringResource(
                        R.string.temperature,
                        weatherState!!.temperature?.let { String.format("%.1f", it) } ?: "--"
                    ),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeMedium
                )
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