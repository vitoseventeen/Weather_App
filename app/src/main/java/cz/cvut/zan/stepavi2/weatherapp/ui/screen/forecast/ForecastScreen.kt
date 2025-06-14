package cz.cvut.zan.stepavi2.weatherapp.ui.screen.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModelFactory
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil
import cz.cvut.zan.stepavi2.weatherapp.util.WeatherUtils
import kotlinx.coroutines.launch

@Composable
fun ForecastScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository(context)
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModelFactory(weatherRepository)
    )
    val viewModel: ForecastViewModel = viewModel(
        factory = ForecastViewModelFactory(context, sharedViewModel)
    )

    val forecastCityInput by sharedViewModel.forecastCityInput.collectAsState()
    val forecastCityToDisplay by sharedViewModel.forecastCityToDisplay.collectAsState()
    val forecastState by sharedViewModel.forecastState.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )

    var validationError by remember { mutableStateOf<String?>(null) }
    var isContentVisible by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (forecastCityToDisplay.isNotEmpty() && forecastState == null) {
            println("Restoring forecast for city: $forecastCityToDisplay on screen recreation")
            viewModel.loadForecast(forecastCityToDisplay)
        }
        isContentVisible = true
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
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Column(
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
                        modifier = Modifier.padding(Dimens.PaddingMedium)
                    ) {
                        OutlinedTextField(
                            value = forecastCityInput,
                            onValueChange = {
                                sharedViewModel.updateForecastCityInput(it)
                                validationError = ValidationUtil.getCityValidationError(it)
                                sharedViewModel.clearError()
                            },
                            label = { Text(stringResource(R.string.enter_city_name)) },
                            modifier = Modifier
                                .weight(1f)
                                .onKeyEvent { event ->
                                    if (event.key == Key.Enter) {
                                        if (ValidationUtil.isValidCityName(forecastCityInput)) {
                                            coroutineScope.launch {
                                                val cityExists = sharedViewModel.checkCityExists(forecastCityInput)
                                                if (cityExists) {
                                                    viewModel.loadForecast(forecastCityInput)
                                                    sharedViewModel.updateForecastCityToDisplay(forecastCityInput)
                                                    keyboardController?.hide()
                                                    focusManager.clearFocus()
                                                }
                                            }
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                },
                            isError = validationError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
                        Button(
                            onClick = {
                                validationError = ValidationUtil.getCityValidationError(forecastCityInput)
                                if (validationError == null) {
                                    coroutineScope.launch {
                                        val cityExists = sharedViewModel.checkCityExists(forecastCityInput)
                                        if (cityExists) {
                                            println("Loading forecast for city: $forecastCityInput")
                                            viewModel.loadForecast(forecastCityInput)
                                            sharedViewModel.updateForecastCityToDisplay(forecastCityInput)
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(start = Dimens.PaddingSmall)
                                .scale(
                                    animateFloatAsState(
                                        targetValue = if (ValidationUtil.isValidCityName(forecastCityInput)) 1f else 0.95f,
                                        animationSpec = tween(200)
                                    ).value
                                ),
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
                            modifier = Modifier.padding(Dimens.PaddingSmall),
                            fontSize = Dimens.TextSizeSmall
                        )
                    }
                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(Dimens.PaddingSmall),
                            fontSize = Dimens.TextSizeSmall
                        )
                    }
                }
            }
            when {
                forecastCityToDisplay.isEmpty() -> {
                    Text(
                        text = "Please enter a city to see the forecast",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = Dimens.PaddingMedium)
                    )
                }
                forecastState?.isNotEmpty() == true -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = Dimens.PaddingMedium),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(forecastState!!) { index, day ->
                            AnimatedVisibility(
                                visible = isContentVisible,
                                enter = fadeIn(animationSpec = tween(300, delayMillis = index * 100)) + scaleIn(initialScale = 0.8f),
                                exit = fadeOut() + scaleOut(targetScale = 0.8f)
                            ) {
                                ForecastDayItem(
                                    day = day,
                                    temperatureUnit = temperatureUnit
                                )
                            }
                        }
                    }
                }
                error == null -> {
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
                WeatherUtils.convertTemperature(temp, temperatureUnit)?.toInt() ?: "--"
            } ?: "--"} / ${day.maxTemperature?.let { temp ->
                WeatherUtils.convertTemperature(temp, temperatureUnit)?.toInt() ?: "--"
            } ?: "--"} ${WeatherUtils.getTemperatureUnitSymbol(temperatureUnit)}",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = Dimens.TextSizeMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            painter = painterResource(id = WeatherUtils.getWeatherIcon(day.weatherCode)),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(Dimens.IconSizeMedium),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}