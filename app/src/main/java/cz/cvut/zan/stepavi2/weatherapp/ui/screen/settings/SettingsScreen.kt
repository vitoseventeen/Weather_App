package cz.cvut.zan.stepavi2.weatherapp.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.cvut.zan.stepavi2.weatherapp.R
import cz.cvut.zan.stepavi2.weatherapp.util.Dimens

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var unit by remember { mutableStateOf("Celsius") }

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
                text = stringResource(R.string.settings),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontSize = Dimens.TextSizeLarge
            )
            Text(
                text = stringResource(R.string.temperature_unit),
                modifier = Modifier.padding(top = Dimens.PaddingMedium),
                fontSize = Dimens.TextSizeMedium
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unit == "Celsius",
                        onClick = { unit = "Celsius" }
                    )
                    Text(
                        text = stringResource(R.string.celsius),
                        fontSize = Dimens.TextSizeMedium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unit == "Fahrenheit",
                        onClick = { unit = "Fahrenheit" }
                    )
                    Text(
                        text = stringResource(R.string.fahrenheit),
                        fontSize = Dimens.TextSizeMedium
                    )
                }
            }
        }
    }
}