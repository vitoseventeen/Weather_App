package cz.cvut.zan.stepavi2.weatherapp.ui.screen.favorites

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
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
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModelFactory
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import cz.cvut.zan.stepavi2.weatherapp.util.ValidationUtil
import cz.cvut.zan.stepavi2.weatherapp.util.WeatherUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModelFactory(weatherRepository)
    )
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val weatherData by viewModel.weatherData.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val cityInput by sharedViewModel.favoritesCityInput.collectAsState()
    val temperatureUnit by viewModel.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )
    val alarmResult by viewModel.alarmResult.collectAsState()
    val theme by preferencesManager.themeFlow.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
    var validationError by remember { mutableStateOf<String?>(null) }

    var cityToRemove by remember { mutableStateOf<String?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var cityForAlarm by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPastTimeError by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember { mutableStateOf(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        Log.d("FavoritesScreen", "Notification permission granted: $isGranted")
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        hasNotificationPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        Log.d("FavoritesScreen", "Notification permission after app settings: $hasNotificationPermission")
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(alarmResult) {
        alarmResult?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearAlarmResult()
        }
    }

    if (showRemoveDialog && cityToRemove != null) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false; cityToRemove = null },
            title = { Text(stringResource(R.string.confirm_deletion)) },
            text = { Text(stringResource(R.string.delete_city_confirmation, cityToRemove!!)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.removeCity(cityToRemove!!)
                    showRemoveDialog = false
                    cityToRemove = null
                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false; cityToRemove = null }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    if (showDatePicker && cityForAlarm != null) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis ?: Calendar.getInstance().timeInMillis
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker && cityForAlarm != null) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Set Time for $cityForAlarm") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                Button(onClick = {
                    val currentTime = System.currentTimeMillis()
                    val dateMillis = selectedDateMillis ?: currentTime

                    val selectedDateCalendar = Calendar.getInstance().apply {
                        timeInMillis = dateMillis
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val todayCalendar = Calendar.getInstance().apply {
                        timeInMillis = currentTime
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val isToday = selectedDateCalendar.timeInMillis == todayCalendar.timeInMillis

                    val selectedCalendar = if (isToday) {
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                    } else {
                        Calendar.getInstance().apply {
                            timeInMillis = dateMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                    }

                    val selectedTime = selectedCalendar.timeInMillis
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    Log.d("FavoritesScreen", "Current time: ${dateFormat.format(currentTime)}")
                    Log.d("FavoritesScreen", "Selected time: ${dateFormat.format(selectedTime)}")

                    if (selectedTime < currentTime) {
                        Log.d("FavoritesScreen", "Selected time is in the past")
                        showTimePicker = false
                        showPastTimeError = true
                    } else {
                        viewModel.setWeatherAlarm(cityForAlarm!!, selectedTime)
                        Log.d("FavoritesScreen", "Alarm set for ${dateFormat.format(selectedTime)}")
                        showTimePicker = false
                        cityForAlarm = null
                    }
                    selectedDateMillis = null
                }) { Text("Set Alarm") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    if (showPastTimeError) {
        AlertDialog(
            onDismissRequest = { showPastTimeError = false },
            title = { Text("Invalid Time") },
            text = { Text("The selected time is in the past. Please choose a future time.") },
            confirmButton = {
                TextButton(onClick = {
                    showPastTimeError = false
                    showTimePicker = true
                }) { Text("OK") }
            }
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Please grant permission to send notifications in the app settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    if (!hasNotificationPermission) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        settingsLauncher.launch(intent)
                    }
                }) { Text("Go to Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("Cancel") }
            }
        )
    }

    val iconTint = when (theme) {
        PreferencesManager.THEME_LIGHT -> Color.White
        PreferencesManager.THEME_DARK -> Color.Black
        PreferencesManager.THEME_SYSTEM -> if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Color.White else Color.Black
        else -> Color.Black
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(Dimens.PaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { newValue ->
                        sharedViewModel.updateFavoritesCityInput(newValue)
                        validationError = ValidationUtil.getCityValidationError(newValue)
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
                Button(
                    onClick = {
                        validationError = ValidationUtil.getCityValidationError(cityInput)
                        if (validationError == null) {
                            coroutineScope.launch {
                                val cityExists = sharedViewModel.checkCityExists(cityInput)
                                if (cityExists) {
                                    viewModel.addCity(cityInput)
                                    sharedViewModel.clearFavoritesCityInput()
                                    focusManager.clearFocus()
                                }
                            }
                        }
                    },
                    enabled = ValidationUtil.isValidCityName(cityInput)
                ) { Text(stringResource(R.string.add)) }
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
            Button(
                onClick = { viewModel.refreshAllWeather(favorites) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingMedium)
            ) { Text(stringResource(R.string.refresh_all)) }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
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
                                    modifier = Modifier.size(Dimens.IconSizeSmall),
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
                            onClick = { cityToRemove = city; showRemoveDialog = true },
                            modifier = Modifier.padding(start = Dimens.PaddingSmall)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_trash),
                                contentDescription = stringResource(R.string.remove),
                                modifier = Modifier.size(Dimens.IconSizeSmall),
                                colorFilter = ColorFilter.tint(iconTint)
                            )
                        }
                        Button(
                            onClick = {
                                if (hasNotificationPermission) {
                                    cityForAlarm = city
                                    showDatePicker = true
                                } else {
                                    cityForAlarm = city
                                    showPermissionDialog = true
                                }
                            },
                            modifier = Modifier.padding(start = Dimens.PaddingSmall)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_alarm),
                                contentDescription = "Set Alarm",
                                modifier = Modifier.size(Dimens.IconSizeSmall),
                                colorFilter = ColorFilter.tint(iconTint)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(Dimens.BottomNavHeight + 16.dp)) }
            }
        }
    }
}