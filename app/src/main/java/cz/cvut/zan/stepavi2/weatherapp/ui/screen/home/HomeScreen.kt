package cz.cvut.zan.stepavi2.weatherapp.ui.screen.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
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
fun HomeScreen(
    onCityClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository(context)
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context)
    )
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModelFactory(weatherRepository)
    )
    val weatherState by viewModel.weather.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )
    val cityInput by sharedViewModel.homeCityInput.collectAsState()

    var validationError by remember { mutableStateOf<String?>(null) }
    var isWeatherVisible by remember { mutableStateOf(false) }

    val locationPermissionState = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionState.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationPermissionState.value && cityInput.isEmpty()) {
            viewModel.loadWeatherForCurrentLocation()
        }
    }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionState.value = true
            if (cityInput.isEmpty()) {
                viewModel.loadWeatherForCurrentLocation()
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        isWeatherVisible = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.current_weather),
                style = MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Dimens.PaddingMedium)
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = {
                        sharedViewModel.updateHomeCityInput(it)
                        validationError = ValidationUtil.getCityValidationError(it)
                        sharedViewModel.clearError()
                    },
                    label = { Text(stringResource(R.string.enter_city_name)) },
                    modifier = Modifier.weight(1f),
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
                        validationError = ValidationUtil.getCityValidationError(cityInput)
                        if (validationError == null) {
                            coroutineScope.launch {
                                val cityExists = sharedViewModel.checkCityExists(cityInput)
                                if (cityExists) {
                                    println("Loading weather for city: $cityInput")
                                    viewModel.loadWeather(cityInput)
                                    focusManager.clearFocus()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(start = Dimens.PaddingSmall)
                        .scale(
                            animateFloatAsState(
                                targetValue = if (ValidationUtil.isValidCityName(cityInput)) 1f else 0.95f,
                                animationSpec = tween(200)
                            ).value
                        ),
                    enabled = ValidationUtil.isValidCityName(cityInput)
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
                    modifier = Modifier.padding(bottom = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            AnimatedVisibility(
                visible = isWeatherVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(Dimens.IconSizeLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = weatherState?.weatherCode?.let { WeatherUtils.getWeatherIcon(it) } ?: R.drawable.ic_sunny
                            ),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(Dimens.IconSizeMedium),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                    if (weatherState != null) {
                        Text(
                            text = weatherState!!.city,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = Dimens.TextSizeMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                        val temperature = WeatherUtils.convertTemperature(weatherState!!.temperature, temperatureUnit)
                        Text(
                            text = stringResource(
                                R.string.temperature,
                                temperature?.let { String.format("%.1f", it) } ?: "--",
                                WeatherUtils.getTemperatureUnitSymbol(temperatureUnit)
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = Dimens.TextSizeMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
                        Button(
                            onClick = {
                                validationError = ValidationUtil.getCityValidationError(cityInput)
                                if (validationError == null) {
                                    coroutineScope.launch {
                                        val cityExists = sharedViewModel.checkCityExists(cityInput)
                                        if (cityExists) {
                                            println("Refreshing weather for city: $cityInput")
                                            viewModel.loadWeather(cityInput)
                                            focusManager.clearFocus()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = Dimens.PaddingMedium),
                            enabled = ValidationUtil.isValidCityName(cityInput)
                        ) {
                            Text(
                                text = stringResource(R.string.refresh_weather),
                                fontSize = Dimens.TextSizeMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                        Button(
                            onClick = { onCityClick(weatherState!!.city) },
                            modifier = Modifier.padding(top = Dimens.PaddingSmall)
                        ) {
                            Text(
                                text = stringResource(R.string.see_details),
                                fontSize = Dimens.TextSizeMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else if (error == null) {
                        Text(
                            text = stringResource(R.string.loading),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = Dimens.TextSizeMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}