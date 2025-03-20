package cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens
import cz.cvut.zan.stepavi2.weatherapp.util.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
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

    Scaffold(
        modifier = modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (temperatureUnit == PreferencesManager.FAHRENHEIT),
                            onClick = {
                                coroutineScope.launch {
                                    preferencesManager.saveTemperatureUnit(PreferencesManager.FAHRENHEIT)
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.PaddingLarge))

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
                        color = MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}