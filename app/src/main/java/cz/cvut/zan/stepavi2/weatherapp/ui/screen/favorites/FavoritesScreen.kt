package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.database.AppDatabase
import cz.cvut.zan.stepavi2.weatherapp.data.repository.CityRepository
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil
import cz.cvut.zan.stepavi2.weatherapp.util.WeatherUtils

@Composable
fun FavoritesScreen(
    onCityClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cityDao = AppDatabase.getDatabase(context).cityDao()
    val cityRepository = CityRepository(cityDao)
    val weatherRepository = WeatherRepository(context)
    val preferencesManager = PreferencesManager(context)
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(cityRepository, weatherRepository, preferencesManager)
    )
    val sharedViewModel: SharedViewModel = viewModel()
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val weatherData by viewModel.weatherData.collectAsState()
    val error by viewModel.error.collectAsState()
    val cityInput by sharedViewModel.favoritesCityInput.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )
    var validationError by remember { mutableStateOf<String?>(null) }

    var cityToRemove by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val density = LocalDensity.current

    if (showDialog && cityToRemove != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                cityToRemove = null
            },
            title = {
                Text(
                    text = stringResource(R.string.confirm_deletion),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_city_confirmation, cityToRemove!!),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeCity(cityToRemove!!)
                        showDialog = false
                        cityToRemove = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        cityToRemove = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.no),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        )
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
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingMedium),
                fontSize = Dimens.TextSizeLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.PaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { newValue ->
                        sharedViewModel.updateFavoritesCityInput(newValue)
                        validationError = ValidationUtil.getCityValidationError(newValue)
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
                Button(
                    onClick = {
                        validationError = ValidationUtil.getCityValidationError(cityInput)
                        if (validationError == null) {
                            viewModel.addCity(cityInput)
                            sharedViewModel.clearFavoritesCityInput()
                            focusManager.clearFocus()
                        }
                    },
                    enabled = ValidationUtil.isValidCityName(cityInput)
                ) {
                    Text(
                        text = stringResource(R.string.add),
                        fontSize = Dimens.TextSizeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(bottom = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            Button(
                onClick = {
                    viewModel.refreshAllWeather(favorites)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.PaddingMedium)
            ) {
                Text(
                    text = stringResource(R.string.refresh_all),
                    fontSize = Dimens.TextSizeMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(bottom = Dimens.PaddingSmall),
                    fontSize = Dimens.TextSizeSmall
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.PaddingSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { onCityClick(city) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val weather = weatherData[city]
                                Image(
                                    painter = painterResource(
                                        id = weather?.weatherCode?.let { WeatherUtils.getWeatherIcon(it) }
                                            ?: R.drawable.ic_sunny
                                    ),
                                    contentDescription = "Weather Icon",
                                    modifier = Modifier.size(Dimens.IconSizeMedium),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                                )
                                Text(
                                    text = city,
                                    fontSize = Dimens.TextSizeMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = weather?.temperature?.let { temp ->
                                        val convertedTemp = WeatherUtils.convertTemperature(temp, temperatureUnit)
                                        "${convertedTemp?.toInt() ?: "--"} ${WeatherUtils.getTemperatureUnitSymbol(temperatureUnit)}"
                                    } ?: "--",
                                    fontSize = Dimens.TextSizeMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Button(
                            onClick = {
                                cityToRemove = city
                                showDialog = true
                            },
                            modifier = Modifier.padding(start = Dimens.PaddingSmall)
                        ) {
                            Text(
                                text = stringResource(R.string.remove),
                                fontSize = Dimens.TextSizeMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Dimens.BottomNavHeight + 16.dp))
                }
            }
        }
    }
}