package cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    var expanded by remember { mutableStateOf(false) }

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
                fontSize = Dimens.TextSizeLarge
            )
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = temperatureUnit,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = Dimens.TextSizeMedium
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(PreferencesManager.CELSIUS) },
                    onClick = {
                        coroutineScope.launch {
                            preferencesManager.saveTemperatureUnit(PreferencesManager.CELSIUS)
                        }
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(PreferencesManager.FAHRENHEIT) },
                    onClick = {
                        coroutineScope.launch {
                            preferencesManager.saveTemperatureUnit(PreferencesManager.FAHRENHEIT)
                        }
                        expanded = false
                    }
                )
            }
        }
    }
}