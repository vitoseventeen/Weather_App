package cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.data.repository.WeatherRepository
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModel
import cz.cvut.zan.stepavi2.weatherapp.ui.screen.shared.SharedViewModelFactory
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel = viewModel(factory = SharedViewModelFactory(WeatherRepository(LocalContext.current)))
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val coroutineScope = rememberCoroutineScope()

    val temperatureUnit by preferencesManager.temperatureUnitFlow.collectAsState(
        initial = PreferencesManager.CELSIUS
    )
    val theme by preferencesManager.themeFlow.collectAsState(
        initial = PreferencesManager.THEME_SYSTEM
    )
    val forecastCity by sharedViewModel.forecastCityToDisplay.collectAsState()

    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isContentVisible = true
    }

    Scaffold(
        modifier = modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.temperature_unit),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = Dimens.TextSizeLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Column(
                        modifier = Modifier
                            .selectableGroup()
                            .padding(top = Dimens.PaddingMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (temperatureUnit == PreferencesManager.CELSIUS),
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.saveTemperatureUnit(PreferencesManager.CELSIUS)
                                            if (forecastCity.isNotEmpty()) {
                                                sharedViewModel.refreshForecastForCity(forecastCity)
                                            }
                                        }
                                    }
                                )
                                .padding(vertical = Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (temperatureUnit == PreferencesManager.CELSIUS),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                            Text(
                                text = PreferencesManager.CELSIUS,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = Dimens.TextSizeMedium,
                                color = animateColorAsState(
                                    targetValue = if (temperatureUnit == PreferencesManager.CELSIUS)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground,
                                    animationSpec = tween(300)
                                ).value
                            )
                        }
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (temperatureUnit == PreferencesManager.FAHRENHEIT),
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.saveTemperatureUnit(PreferencesManager.FAHRENHEIT)
                                            if (forecastCity.isNotEmpty()) {
                                                sharedViewModel.refreshForecastForCity(forecastCity)
                                            }
                                        }
                                    }
                                )
                                .padding(vertical = Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (temperatureUnit == PreferencesManager.FAHRENHEIT),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                            Text(
                                text = PreferencesManager.FAHRENHEIT,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = Dimens.TextSizeMedium,
                                color = animateColorAsState(
                                    targetValue = if (temperatureUnit == PreferencesManager.FAHRENHEIT)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground,
                                    animationSpec = tween(300)
                                ).value
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.PaddingLarge))

            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(animationSpec = tween(300, delayMillis = 200)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.app_theme),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = Dimens.TextSizeLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Column(
                        modifier = Modifier
                            .selectableGroup()
                            .padding(top = Dimens.PaddingMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (theme == PreferencesManager.THEME_SYSTEM),
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.saveTheme(PreferencesManager.THEME_SYSTEM)
                                        }
                                    }
                                )
                                .padding(vertical = Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == PreferencesManager.THEME_SYSTEM),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                            Text(
                                text = stringResource(R.string.theme_system),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = Dimens.TextSizeMedium,
                                color = animateColorAsState(
                                    targetValue = if (theme == PreferencesManager.THEME_SYSTEM)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground,
                                    animationSpec = tween(300)
                                ).value
                            )
                        }
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (theme == PreferencesManager.THEME_LIGHT),
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.saveTheme(PreferencesManager.THEME_LIGHT)
                                        }
                                    }
                                )
                                .padding(vertical = Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == PreferencesManager.THEME_LIGHT),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                            Text(
                                text = stringResource(R.string.theme_light),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = Dimens.TextSizeMedium,
                                color = animateColorAsState(
                                    targetValue = if (theme == PreferencesManager.THEME_LIGHT)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground,
                                    animationSpec = tween(300)
                                ).value
                            )
                        }
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (theme == PreferencesManager.THEME_DARK),
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.saveTheme(PreferencesManager.THEME_DARK)
                                        }
                                    }
                                )
                                .padding(vertical = Dimens.PaddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == PreferencesManager.THEME_DARK),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                            Text(
                                text = stringResource(R.string.theme_dark),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = Dimens.TextSizeMedium,
                                color = animateColorAsState(
                                    targetValue = if (theme == PreferencesManager.THEME_DARK)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onBackground,
                                    animationSpec = tween(300)
                                ).value
                            )
                        }
                    }
                }
            }
        }
    }
}