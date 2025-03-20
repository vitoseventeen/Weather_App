package cz.cvut.zan.stepavi2.weatherapp.ui.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetailScreen(
    city: String,
    paddingValues: PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                .padding(
                    top = Dimens.PaddingMedium,
                    start = Dimens.PaddingMedium,
                    end = Dimens.PaddingMedium
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.weather_in, city),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = Dimens.TextSizeLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(Dimens.PaddingMedium))
                if (errorState != null) {
                    Text(
                        text = errorState!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium
                    )
                } else if (weatherState != null) {
                    Image(
                        painter = painterResource(id = getWeatherIcon(weatherState!!.weatherCode)),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(Dimens.IconSizeLarge),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
                    Text(
                        text = stringResource(
                            R.string.condition,
                            weatherState!!.condition ?: stringResource(R.string.unknown)
                        ),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
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
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.humidity, weatherState!!.humidity),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.pressure, weatherState!!.pressure.toInt()),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(
                            R.string.wind,
                            String.format("%.1f", weatherState!!.windspeed),
                            getWindDirection(weatherState!!.winddirection)
                        ),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Column(
                        modifier = Modifier
                            .padding(top = Dimens.PaddingMedium)
                            .padding(horizontal = Dimens.PaddingSmall),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = Dimens.PaddingSmall)
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sunrise),
                                contentDescription = "Sunrise Icon",
                                modifier = Modifier
                                    .size(Dimens.IconSizeSmall)
                                    .padding(end = Dimens.PaddingSmall),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.sunrise_label),
                                    fontSize = Dimens.TextSizeMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = formatTime(weatherState!!.sunrise),
                                    fontSize = Dimens.TextSizeMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = Dimens.PaddingSmall)
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sunset),
                                contentDescription = "Sunset Icon",
                                modifier = Modifier
                                    .size(Dimens.IconSizeSmall)
                                    .padding(end = Dimens.PaddingSmall),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.sunset_label),
                                    fontSize = Dimens.TextSizeMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = formatTime(weatherState!!.sunset),
                                    fontSize = Dimens.TextSizeMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.loading),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                Text(
                    text = stringResource(R.string.back),
                    fontSize = Dimens.TextSizeMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
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

fun formatTime(time: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(time)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "N/A"
    }
}

fun getWindDirection(degrees: Int): String {
    return when (degrees) {
        in 0..22, in 338..360 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"
        else -> "Unknown"
    }
}