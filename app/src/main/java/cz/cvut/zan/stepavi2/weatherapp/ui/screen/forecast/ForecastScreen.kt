package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil

@Composable
fun ForecastScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = viewModel()
    val viewModel: ForecastViewModel = viewModel(
        factory = ForecastViewModelFactory(context)
    )

    val forecastCityInput by sharedViewModel.forecastCityInput.collectAsState()
    val forecastCityToDisplay by sharedViewModel.forecastCityToDisplay.collectAsState()
    val savedForecastState by sharedViewModel.forecastState.collectAsState()

    val forecastState by viewModel.forecast.collectAsState()
    val errorState by viewModel.error.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )

    var validationError by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(forecastState) {
        sharedViewModel.updateForecastState(forecastState)
        forecastState?.let {
            println("ForecastScreen: Number of forecast days: ${it.size}")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = Dimens.PaddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (forecastCityToDisplay.isNotEmpty()) {
                    stringResource(R.string.forecast_title, forecastCityToDisplay)
                } else {
                    "Week Forecast"
                },
                style = MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Dimens.PaddingMedium)
            ) {
                TextField(
                    value = forecastCityInput,
                    onValueChange = {
                        sharedViewModel.updateForecastCityInput(it)
                        validationError = ValidationUtil.getCityValidationError(it)
                    },
                    label = { Text(stringResource(R.string.enter_city_name)) },
                    modifier = Modifier
                        .weight(1f)
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter) {
                                if (ValidationUtil.isValidCityName(forecastCityInput)) {
                                    viewModel.loadForecast(forecastCityInput)
                                    sharedViewModel.updateForecastCityToDisplay(forecastCityInput)
                                    keyboardController?.hide()
                                }
                                true
                            } else {
                                false
                            }
                        },
                    isError = validationError != null
                )
                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
                Button(
                    onClick = {
                        validationError = ValidationUtil.getCityValidationError(forecastCityInput)
                        if (validationError == null) {
                            println("Loading forecast for city: $forecastCityInput")
                            viewModel.loadForecast(forecastCityInput)
                            sharedViewModel.updateForecastCityToDisplay(forecastCityInput)
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier.padding(start = Dimens.PaddingSmall),
                    enabled = ValidationUtil.isValidCityName(forecastCityInput)
                ) {
                    Text(
                        text = stringResource(R.string.search),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            if (forecastCityToDisplay.isEmpty()) {
                Text(
                    text = "Please enter a city to see the forecast",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = Dimens.TextSizeMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.PaddingMedium)
                )
            } else if (errorState != null) {
                Text(
                    text = errorState!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = Dimens.TextSizeMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.PaddingMedium)
                )
            } else if (forecastState != null && forecastState!!.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = Dimens.PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(forecastState!!) { _, day ->
                        ForecastDayItem(
                            day = day,
                            temperatureUnit = temperatureUnit
                        )
                    }
                }
            } else if (savedForecastState != null && savedForecastState!!.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = Dimens.PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(savedForecastState!!) { _, day ->
                        ForecastDayItem(
                            day = day,
                            temperatureUnit = temperatureUnit
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = Dimens.TextSizeMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.PaddingMedium)
                )
            }
        }
    }
}

@Composable
fun ForecastDayItem(
    day: ForecastDay,
    temperatureUnit: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.date,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = Dimens.TextSizeMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${day.minTemperature?.let { temp ->
                if (temperatureUnit == PreferencesManager.FAHRENHEIT) (temp * 9 / 5 + 32).toInt() else temp.toInt()
            } ?: "--"}° / ${day.maxTemperature?.let { temp ->
                if (temperatureUnit == PreferencesManager.FAHRENHEIT) (temp * 9 / 5 + 32).toInt() else temp.toInt()
            } ?: "--"}° ${if (temperatureUnit == PreferencesManager.FAHRENHEIT) "°F" else "°C"}",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = Dimens.TextSizeMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            painter = painterResource(id = getWeatherIcon(day.weatherCode)),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(Dimens.IconSizeMedium),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
fun getWeatherIcon(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.drawable.ic_sunny
        1, 2, 3 -> R.drawable.ic_cloudy
        45, 48 -> R.drawable.ic_mist
        51, 53, 55 -> R.drawable.ic_drizzle
        61, 63, 65 -> R.drawable.ic_rain
        71, 73, 75 -> R.drawable.ic_snow
        95 -> R.drawable.ic_thunderstorm
        else -> R.drawable.ic_sunny
    }
}